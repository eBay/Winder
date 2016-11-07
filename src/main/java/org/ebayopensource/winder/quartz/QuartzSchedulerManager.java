/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.quartz;

import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.common.config.InjectProperty;
import org.ebayopensource.winder.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.ebayopensource.winder.StatusEnum.CANCELLED;
import static org.ebayopensource.winder.StatusEnum.PAUSED;
import static org.ebayopensource.winder.StatusEnum.SUBMITTED;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzSchedulerManager<TI extends TaskInput> implements WinderSchedulerManager<TI> {

    private Scheduler quartzScheduler = null;

    private WinderEngine engine;

    @InjectProperty(name="winder.scheduler.step_interval")
    private int defaultStepInterval = 10; //Seconds

    @InjectProperty(name="winder.scheduler.max_job_duration")
    private int defaultMaxJobDuration = (int)TimeUnit.DAYS.toMillis(7); //Seconds

    private static Logger log = LoggerFactory.getLogger(QuartzSchedulerManager.class);

    private WinderJobDetailFactory jobDetailFactory;

    public QuartzSchedulerManager(WinderEngine engine, Scheduler quartzScheduler) {
        this.engine = engine;
        this.jobDetailFactory = engine.getJobDetailFactory();
        this.quartzScheduler = quartzScheduler;
    }

    public int getDefaultStepInterval() {
        return defaultStepInterval;
    }

    public void setDefaultStepInterval(int defaultStepInterval) {
        this.defaultStepInterval = defaultStepInterval;
    }

    public long getDefaultMaxJobDuration() {
        return defaultMaxJobDuration;
    }

    public void setDefaultMaxJobDuration(int defaultMaxJobDuration) {
        this.defaultMaxJobDuration = defaultMaxJobDuration;
    }

    @Override
    public WinderJobDetail getJobDetail(JobId jobId) throws WinderScheduleException {
        JobKey key = getKey(jobId);
        try {
            JobDetail qjd = quartzScheduler.getJobDetail(key);

            if (qjd instanceof WinderJobDetail) {
                return (WinderJobDetail)qjd;
            }
            else {
                return new QuartzJobDetail(engine, jobId, qjd);
            }
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Retrieving job detail error", e);
        }
    }

    protected JobKey getKey(JobId jobId) {
        if (jobId instanceof QuartzJobId) {
            return ((QuartzJobId)jobId).getKey();
        }
        else {
            return new JobKey(jobId.getName(), jobId.getGroup());
        }
    }

    @Override
    public void unscheduleJob(JobId jobId) throws WinderScheduleException {
        List<? extends Trigger> triggers = null;

        try {
            triggers = quartzScheduler.getTriggersOfJob(getKey(jobId));
        }
        catch(SchedulerException se) {
            throw new WinderScheduleException("Querying triggers exception", se);
        }
        if (triggers == null) {
            return;
        }
        for (Trigger trigger : triggers) {
            try {
                quartzScheduler.unscheduleJob(trigger.getKey());
            } catch (SchedulerException e) {
                throw new WinderScheduleException("Unscheduleing job exception", e);
            }
        }
    }

    @Override
    public void updateJobData(WinderJobDetail job) {

    }

    @Override
    public JobId scheduleChildJob(TI input, WinderJobContext parentJobCtx) throws WinderScheduleException {

        Class clazz = input.getJobClass();
        if (clazz == null) {
            throw new IllegalArgumentException("Need job class ");
        }
        String owner = parentJobCtx.getJobStateData().getOwner();
        if (input.getJobOwner() == null) {
            input.setJobOwner(owner);
        }

        WinderJobDetail jd = jobDetailFactory.createJobDetail(input);

        if (jd instanceof QuartzJobDetail) {
            ((QuartzJobDetail)jd).setParentJobId(parentJobCtx.getJobId());
        }

        JobId jobId = jd.getJobId();

        // Create trigger
        Trigger t = createStagedTrigger(input.getStepInterval(), input.getJobDuration(), new Date(), jobId);


        // Child job was scheduled, add child job id to list in parent context
        parentJobCtx.getJobDetail().addChildJobIds(jd.getJobId());

        // schedule job
        try {
            quartzScheduler.scheduleJob((JobDetail)jd, t);
            updateJobDetail(parentJobCtx.getJobDetail());
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Error scheduling job", e);
        }

        return jobId;
    }

    @Override
    public boolean doneYet(List<WinderJobDetail> childJobDetails) {
        if (childJobDetails == null) {
            throw new IllegalArgumentException("Details array cannot be null");
        }
        for (WinderJobDetail detail : childJobDetails) {
            if (detail == null) {
                throw new IllegalStateException("Details cannot be null");
            }
            switch (detail.getStatus()) {
                case EXECUTING:
                case CANCEL_IN_PROGRESS:
                case PAUSED:    // assumption that CANCEL if really done
                case SUBMITTED:
                    return false;  // these mean the job isn't done
                default:
                    continue;  // keep checking
            }
        }
        return true;
    }

    @Override
    public JobId scheduleJob(TI input) throws WinderScheduleException {
        WinderJobDetail jd = jobDetailFactory.createJobDetail(input);

        Date jobStartTime = input.getJobStartTime();
        Trigger t = createStagedTrigger(input.getStepInterval(),
                input.getJobDuration(), jobStartTime, jd.getJobId());
        try {
            quartzScheduler.scheduleJob((JobDetail)jd, t);
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Scheduling simple job exception", e);
        }
        return jd.getJobId();
    }

    protected String triggerName(JobId jobId) {
        return TRIGGER_NAME_PREFIX + jobId.getName();
    }

    protected String cronJobTriggerName(JobId jobId) {
        return TRIGGER_NAME_PREFIX + jobId.toString();
    }

    protected Trigger createStagedTrigger(int stageIntervalSec, int maxTimeForJobSec, Date startTime, JobId jobId) {
        return TriggerBuilder.newTrigger().withIdentity(triggerName(jobId), jobId.getGroup())
                .forJob(jobId.getName(), jobId.getGroup()).startAt(startTime)
                .endAt(new Date(startTime.getTime() + maxTimeForJobSec * 1000))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(stageIntervalSec).repeatForever())
                .build();
    }

    @Override
    public void rescheduleJob(JobId jobId, Date jobStartTime, int stageIntervalSec, int maxTimeForJobSec) throws WinderScheduleException {
        if (jobStartTime == null) {
            jobStartTime = new Date();
        }
        String triggerName = triggerName(jobId);
        String triggerGroup = jobId.getGroup();

        Trigger t = createStagedTrigger(stageIntervalSec, maxTimeForJobSec, jobStartTime, jobId);
        rescheduleJob(triggerName, triggerGroup, jobId, jobStartTime, t, true, null);
    }

    @Override
    public void rescheduleCronJob(JobId jobId, Date jobStartTime, String cronExpression) throws WinderScheduleException {
        if (jobStartTime == null) {
            jobStartTime = new Date();
        }

        String triggerName = cronJobTriggerName(jobId);
        String triggerGroup = TRIGGER_GROUP_CRON;

        Trigger t = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
                .forJob(jobId.getName(), jobId.getGroup()).startAt(jobStartTime)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        rescheduleJob(triggerName, triggerGroup, jobId, jobStartTime, t, true, null);
    }

    private void rescheduleJob(String triggerName, String triggerGroup, JobId jobId, Date jobStartTime, Trigger t,
                               boolean hasTrigger, WinderJobDetail jobDetails)

            throws WinderScheduleException
    {

        JobDetail jd = null;
        if (jobDetails == null) {
            jobDetails = getJobDetail(jobId);
        }

        jd = (JobDetail)jobDetails;

        JobDataMap jobMap = jd.getJobDataMap();
        jobMap.put(KEY_JOBSTARTDATE, engine.formatDate(jobStartTime));

        // reset the status
        changeJobStatus(jobDetails, SUBMITTED);

        try {
            if (hasTrigger) {
                quartzScheduler.rescheduleJob(new TriggerKey(triggerName, triggerGroup), t);
            } else {
                quartzScheduler.scheduleJob(t);
            }
        }
        catch(SchedulerException se) {
            throw new WinderScheduleException("Scheduling job " + jobId + " exception", se);
        }
    }

    @Override
    public JobId scheduleCronJob(TI input, String cronExpression) throws WinderScheduleException {
        WinderJobDetail jd = jobDetailFactory.createJobDetail(input);

        JobId jobId = jd.getJobId();
        Date startTime = input.getJobStartTime();
        Trigger t = TriggerBuilder.newTrigger().withIdentity(cronJobTriggerName(jobId), TRIGGER_GROUP_CRON)
                .forJob(jobId.getName(), jobId.getGroup()).startAt(startTime)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        try {
            quartzScheduler.scheduleJob((JobDetail)jd, t);
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Scheduling cron job exception", e);
        }
        return jobId;
    }

    @Override
    public void pauseJob(JobId jobId) throws WinderScheduleException {
        WinderJobDetail d = checkJobStatusChange(jobId, PAUSED, new StatusEnum[] { StatusEnum.SUBMITTED,
                StatusEnum.PAUSED, StatusEnum.ERROR, StatusEnum.EXECUTING });
        try {
            quartzScheduler.pauseJob(getKey(jobId));
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Pausing job exception", e);
        }
        changeJobStatus(d, StatusEnum.PAUSED);
    }

    @Override
    public void resumeJob(JobId jobId, StatusEnum newStatus, String message, Boolean autoPause, String user) throws WinderScheduleException {

        // Cases																message
        // 1. regular Resume											""
        // 2. Resume with awaitingforAction								""
        // 3. Cancel(by user) with awaitingForAction(during paused)		""
        // 4. Cancel(by LOM) with awaitingForAction(during paused)		"someMessage"
        // 5. Regular Cancel(during paused)								""

        WinderJobDetail jobDetail = checkJobStatusChange(jobId, newStatus, new StatusEnum[]{PAUSED});
        String jobStatusMessage;
        String actionMsg = (newStatus.equals(StatusEnum.CANCEL_IN_PROGRESS)) ? "cancelled" : "resumed";
        jobStatusMessage = "Job " + actionMsg + " by: " + user + ". ";

        if(jobDetail.isAwaitingForAction() || (newStatus.equals(StatusEnum.CANCEL_IN_PROGRESS) && !StringUtils.isEmpty(message))) {
            UserActionType action = (newStatus.equals(StatusEnum.CANCEL_IN_PROGRESS))
                    ? UserActionType.CANCELLED : UserActionType.RESUMED;
            message = StringUtils.isEmpty(message) ? jobStatusMessage : message;
            markAlert(jobId, jobDetail, action, message, user);

            jobDetail.setAwaitingForAction(false);
            updateJobData(jobDetail);
        }
        try {
            quartzScheduler.resumeJob(getKey(jobId));
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Resuming job error", e);
        }

        if (StringUtils.isNotEmpty(jobStatusMessage)) {
            changeJobStatus(jobDetail, newStatus, jobStatusMessage, autoPause);
        } else {
            changeJobStatus(jobDetail, newStatus);
        }
    }

    public void markAlert(JobId jobId, WinderJobDetail jobDetail, UserActionType action,
                          String message, String user) throws WinderScheduleException {

        if(jobDetail == null){
            jobDetail = getJobDetail(jobId);
        }
        boolean awaitingForAction = action.equals(UserActionType.PAUSED);
        jobDetail.setAwaitingForAction(awaitingForAction);

        if(StringUtils.isEmpty(message)){
            message = "Job " + action.toString().toLowerCase() + " by " + user + " .";
        }


        jobDetail.addUserAction(action, message, user);

        if (log.isDebugEnabled()) {
            log.debug("The alert is being updated in job details. JobId : " + jobId +
                    ", action : " + action + ", awaitingForAction : " + awaitingForAction);
        }
        updateJobDetail(jobDetail);
    }


    protected void updateJobDetail(WinderJobDetail jobDetail) throws WinderScheduleException {
        try {
            quartzScheduler.addJob((JobDetail)jobDetail, true);
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Change job data exception", e);
        }
    }

    @Override
    public void cancelJob(JobId jobId, boolean force) throws WinderScheduleException {
        WinderJobDetail jobDetail = checkJobStatusChange(jobId, CANCELLED,
                force ? null
                        : new StatusEnum[] { StatusEnum.SUBMITTED, CANCELLED,
                        StatusEnum.CANCEL_IN_PROGRESS, PAUSED, StatusEnum.ERROR,
                        StatusEnum.UNKNOWN, StatusEnum.EXECUTING });
        unscheduleJob(jobId);
        changeJobStatus(jobDetail, CANCELLED);
    }

    @Override
    public void markCancelInProgress(JobId jobId, String message, String user) throws WinderScheduleException {
       //This will work, check Job for race condition resolution
        WinderJobDetail jobDetail = checkJobStatusChange(jobId, StatusEnum.CANCEL_IN_PROGRESS,
                new StatusEnum[] { StatusEnum.SUBMITTED, StatusEnum.PAUSED, StatusEnum.ERROR,
                        StatusEnum.EXECUTING, StatusEnum.CANCEL_IN_PROGRESS });

        if (StringUtils.isNotEmpty(message)) {
            changeJobStatus(jobDetail, StatusEnum.CANCEL_IN_PROGRESS, "Job Cancelled by: " + user + ".", null);
            markAlert(jobId, jobDetail, UserActionType.CANCELLED, message, user);
        } else {
            changeJobStatus(jobDetail, StatusEnum.CANCEL_IN_PROGRESS);
        }
    }

    @Override
    public boolean successChildJob(List<WinderJobDetail> childJobDetails) {
        if (childJobDetails == null) {
            throw new IllegalArgumentException("Details array cannot be null");
        }
        for (int i = 0; i< childJobDetails.size(); i++) {
            WinderJobDetail detail = childJobDetails.get(i);
            if (detail == null) {
                throw new IllegalStateException("Details cannot be null");
            }
            switch(detail.getStatus()) {
                case COMPLETED :
                case CANCELLED :
                    return true;
                default :
                    return false;
            }
        }
        return true;
    }


    private void changeJobStatus(WinderJobDetail d, StatusEnum newStatus) throws WinderScheduleException {
        changeJobStatus(d, newStatus, null, null);
    }

    private WinderJobDetail checkJobStatusChange(JobId jobId, StatusEnum newStatus, StatusEnum[] oldStatuses)
            throws WinderScheduleException {

        WinderJobDetail jd = getJobDetail(jobId);
        if (jd == null) {
            throw new IllegalArgumentException("job does not exist for id=" + jobId);
        }

        // If old status was not unknown, validate
        if (oldStatuses != null) {
            StatusEnum currentStatus = jd.getStatus();
            boolean match = false;
            StringBuilder possibles = new StringBuilder();
            for (StatusEnum e : oldStatuses) {
                if (e == currentStatus) {
                    match = true;
                    break;
                } else {
                    possibles.append(' ').append(e.name());
                }
            }
            if (!match) {
                String msg;
                if (oldStatuses.length == 1) {
                    msg = "Unexpected status.  Expected" + possibles + " but found " + currentStatus + " for "
                            + jobId;
                } else {
                    msg = "Unexpected status.  Expected one of (" + possibles + " ) but found " + currentStatus
                            + " for " + jobId;
                }
                throw new WinderScheduleException(msg);
            }
        }
        return jd;
    }

    // NOTE: this must be called after checkJobStatusChange is done!!
    private void changeJobStatus(WinderJobDetail d, StatusEnum newStatus,
                                 String jobStatusMessage, Boolean autoPause)
            throws WinderScheduleException {
        if (autoPause != null) {
            d.setAutoPause(autoPause);
        }

        d.setStatus(newStatus);

        JobDetail qjd= (JobDetail)d;
        qjd.getJobDataMap().put(KEY_IS_REPLACE_JOB, "y");

        if (StringUtils.isNotBlank(jobStatusMessage)) {
            d.addUpdate(newStatus, jobStatusMessage);
        }

        if ((CANCELLED == newStatus) || (StatusEnum.ERROR == newStatus)) {
            // set end date if the job is canceled or errored
            String endDateStr = engine.formatDate(new Date());
            d.setEndDate(endDateStr);
        }
        updateJobDetail(d);
    }
}
