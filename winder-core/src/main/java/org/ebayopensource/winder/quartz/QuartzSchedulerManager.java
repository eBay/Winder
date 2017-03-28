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
import org.quartz.impl.DefaultThreadExecutor;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.plugins.history.LoggingJobHistoryPlugin;
import org.quartz.plugins.history.LoggingTriggerHistoryPlugin;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.simpl.SimpleInstanceIdGenerator;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.spi.ThreadPool;
import org.quartz.utils.DBConnectionManager;
import org.quartz.utils.PoolingConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.ebayopensource.winder.StatusEnum.CANCELLED;
import static org.ebayopensource.winder.StatusEnum.PAUSED;
import static org.ebayopensource.winder.StatusEnum.SUBMITTED;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;
import static org.quartz.impl.jdbcjobstore.Constants.COL_JOB_DATAMAP;

/**
 * Schedule Manager
 *
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

    private boolean inMemoryScheduler = false;

    public QuartzSchedulerManager(WinderEngine engine) {
        this.engine = engine;
        this.jobDetailFactory = engine.getJobDetailFactory();
        init();
    }

    private String dataSourceName;

    private void init() {
        WinderConfiguration configuration = engine.getConfiguration();
        DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
        int numThreads = configuration.getInt("winder.quartz.numThreads", 50);


        String quartzType = configuration.getString("winder.quartz.scheduler_type");
        dataSourceName = configuration.getString("winder.quartz.datasource");

        Scheduler scheduler = null;
        boolean inMemoryScheduler = false;
        try {
            if ("IN_MEMORY_SCHEDULER".equals(quartzType) || (quartzType == null && dataSourceName == null)) {
                factory.createVolatileScheduler(numThreads);
                scheduler = factory.getScheduler();

                inMemoryScheduler = true;
                if (log.isInfoEnabled()) {
                    log.info("Scheduler manager starting IN_MEMORY_SCHEDULER");
                }
            } else {
                ThreadPool threadPool = new SimpleThreadPool(numThreads, Thread.NORM_PRIORITY);
                threadPool.initialize();
                String instanceId = (new SimpleInstanceIdGenerator()).generateInstanceId();


                DBConnectionManager dbMgr = DBConnectionManager.getInstance();

                int poolSize = configuration.getInt("winder.quartz.ds.pool_size", numThreads + 15);

                String jdbcUrl = dataSourceName;
                if ("ds".equals(dataSourceName)) {
                    //
                    String jdbcDriver = configuration.getString("winder.quartz.ds.driver");
                    jdbcUrl = configuration.getString("winder.quartz.ds.url");
                    String jdbcUser = configuration.getString("winder.quartz.ds.username");
                    String jdbcPassword = configuration.getString("winder.quartz.ds.password");

                    String validate = configuration.getString("winder.quartz.ds.validate_sql", "SELECT 1 /* ping */");
                    PoolingConnectionProvider pooling = new PoolingConnectionProvider(
                            jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword, poolSize, validate);

                    dbMgr.addConnectionProvider(dataSourceName, pooling);
                }
                else {
                    log.warn("Please make sure the data source:" + dataSourceName + " has already been initialized in somewhere else");
                }

                boolean enableQuartz = configuration.getBoolean("winder.quartz.enable", true);

                if (enableQuartz) {
                    String tablePrefix = configuration.getString("winder.quartz.ds.table_prefix", "WINDER_");

                    reformat(SELECT_JOBS_LIMIT, tablePrefix);
                    reformat(SELECT_JOBS_LIMIT_BY_DATE_RANGE, tablePrefix);
                    reformat(SELECT_JOBS_LIMIT_LIKE, tablePrefix);
                    reformat(SELECT_JOBS_LIMIT_LIKE_BY_DATE_RANGE, tablePrefix);

                    int checkInterval = configuration.getInt("winder.quartz.checkin_interval", 7500);
                    String clusterName = engine.getClusterName();
                    JobStoreTX jdbcJobStore = new WinderJobStoreTx();
                    jdbcJobStore.setDataSource(dataSourceName);
                    jdbcJobStore.setTablePrefix(tablePrefix);
                    jdbcJobStore.setIsClustered(true);
                    jdbcJobStore.setClusterCheckinInterval(checkInterval);

                    String hostName;
                    try {
                        InetAddress inet = InetAddress.getLocalHost();
                        hostName = inet.getHostName();
                    } catch (UnknownHostException e) {
                        hostName = "unknownHost";
                    }
                    jdbcJobStore.setInstanceId(hostName);
                    jdbcJobStore.setDriverDelegateClass("org.ebayopensource.winder.quartz.WinderJDBCDelegate");
                    jdbcJobStore.setThreadPoolSize(poolSize);

                    // To fix the quartz misfire issue
                    DefaultThreadExecutor executor = new DefaultThreadExecutor();
                    long idleWaitTime = configuration.getLong("winder.quartz.idle_wait_time", 30000L);
                    long dbFailureRetryInterval = configuration.getLong("winder.quartz.db_failure_retry_interval",
                            10000L);
                    long batchTimeWindow = configuration.getLong("winder.quartz.batch_time_window", 1000L);

                    boolean enableQuartzPlugins = configuration.getBoolean("winder.quartz.plugins.enable", false);
                    if (enableQuartzPlugins) {
                        Map<String, SchedulerPlugin> schedulerPluginMap = new HashMap<String, SchedulerPlugin>();
                        schedulerPluginMap.put("LoggingTriggerHistoryPlugin", new LoggingTriggerHistoryPlugin());
                        schedulerPluginMap.put("LoggingJobHistoryPlugin", new LoggingJobHistoryPlugin());

                        factory.createScheduler(clusterName, instanceId, threadPool, executor, jdbcJobStore,
                                schedulerPluginMap, null, 0, idleWaitTime, dbFailureRetryInterval, false, null,
                                numThreads, batchTimeWindow);
                    } else {
                        factory.createScheduler(clusterName, instanceId, threadPool, executor, jdbcJobStore, null, null,
                                0, idleWaitTime, dbFailureRetryInterval, false, null, numThreads, batchTimeWindow);
                    }
                    scheduler = factory.getScheduler(clusterName);
                    if (log.isInfoEnabled()) {
                        log.info("Scheduler manager starting with:" + jdbcUrl);
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Scheduler manager disabled!");
                    }
                }
            }

            this.quartzScheduler = scheduler;
            this.inMemoryScheduler = inMemoryScheduler;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failure initializing quartz", e);
            }
            throw new IllegalStateException("Unable to initialize quartz", e);
        }
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

    @Override
    public WinderJobDetail getJobDetail(String jobId) throws WinderScheduleException {
        return getJobDetail(WinderUtil.toJobId(jobId));
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
    public void updateJobData(WinderJobDetail job) throws WinderScheduleException{
        try {
            quartzScheduler.addJob((JobDetail)job, true);
        } catch (SchedulerException e) {
            throw new WinderScheduleException("Change job data exception", e);
        }
    }

    @Override
    public JobId scheduleChildJob(TI input, WinderJobContext parentJobCtx) throws WinderScheduleException {

        Class clazz = input.getJobClass();
        if (clazz == null) {
            throw new IllegalArgumentException("Need job class ");
        }
        String owner = parentJobCtx.getJobSummary().getOwner();
        if (input.getJobOwner() == null) {
            input.setJobOwner(owner);
        }

        WinderJobDetail jd = jobDetailFactory.createJobDetail(input);

        if (jd instanceof QuartzJobDetail) {
            ((QuartzJobDetail)jd).setParentJobId(parentJobCtx.getJobId());
        }

        JobId jobId = jd.getJobId();

        // Create trigger
        Trigger t = createStagedTrigger(input.getStepInterval(), input.getJobDuration(),
                input.getJobScheduleTime(), jobId);


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

        Date jobStartTime = input.getJobScheduleTime();
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
        jobMap.put(KEY_JOB_START_DATE, jobStartTime.getTime());

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
        Date startTime = input.getJobScheduleTime();
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
        // 3. Cancel(by owner) with awaitingForAction(during paused)		""
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

    @Override
    public List<WinderJobDetail> listJobDetails(JobFilter filter) throws WinderScheduleException {
        if (!inMemoryScheduler) {
            return selectJobs(filter);
        }
        else {
            return fetchInMemory(filter);
        }
    }

    public List<WinderJobDetail> fetchInMemory(JobFilter filter) throws WinderScheduleException {

        List<WinderJobDetail> jobs = new ArrayList<>();
        // start making queries
        boolean hasDateRange = false;

        Date start = filter.getStart();
        long startTime = 0, endTime = 0;
        if (start != null) {
            hasDateRange = true;

            startTime = start.getTime();

            endTime = (filter.getEnd() == null) ? System.currentTimeMillis()
                    : filter.getEnd().getTime();
        }

        try {
            List<String> groupNames = quartzScheduler.getJobGroupNames();
            for (String group : groupNames) {
                JobKeyField keyField = filter.getKeyField();

                boolean matched = false;
                if (keyField == JobKeyField.JOB_GROUP) {
                    if (filter.isLike()) {
                        matched = group.contains(filter.getValue());
                    } else {
                        matched = group.equals(filter.getValue());
                    }
                }
                else if (keyField == JobKeyField.ALL) {
                    matched = true;
                }

                GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(group);
                Set<JobKey> keys = quartzScheduler.getJobKeys(groupMatcher);

                for (JobKey key : keys) {
                    if (!matched) { //Job NAME
                        String jobName = key.getName();
                        if (filter.isLike()) {
                            matched = jobName.contains(filter.getValue());
                        } else {
                            matched = jobName.equals(filter.getValue());
                        }
                    }
                    if (matched) {
                        WinderJobDetail jobDetail = getJobDetail(new QuartzJobId(key, engine.getClusterName()));

                        if (hasDateRange) {
                            long date = jobDetail.getCreated().getTime();
                            if (date >= startTime && date < endTime) {
                                jobs.add(jobDetail);
                            }
                        }
                        else {
                            jobs.add(jobDetail);
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            log.error("Error fetching groups & jobs ", ex);
            throw new WinderScheduleException("Error fetching groups & jobs ", ex);
        }
        return limit(jobs, filter);
    }

    private static void reformat(String[] array, String prefix) {
        for(int i = 0; i < array.length; i ++) {
            array[i] = array[i].replace("{0}", prefix);
        }
    }

    private final String[] SELECT_JOBS_LIMIT = new String[] {
            "SELECT * FROM {0}JOB_DETAILS ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_NAME = ? ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_GROUP = ? ORDER BY JOB_CREATED DESC LIMIT ?, ?"
    };

    private final String[] SELECT_JOBS_LIMIT_BY_DATE_RANGE = new String[] {
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_CREATED >= ? AND JOB_CREATED < ? ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_NAME = ? AND JOB_CREATED >= ? AND JOB_CREATED < ? ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_GROUP = ? AND JOB_CREATED >= ? AND JOB_CREATED < ? ORDER BY JOB_CREATED DESC LIMIT ?, ?"
    };

    private final String[] SELECT_JOBS_LIMIT_LIKE = new String[] {
            "SELECT * FROM {0}JOB_DETAILS ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_NAME LIKE '%?%' ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_GROUP LIKE '%?%' ORDER BY JOB_CREATED DESC LIMIT ?, ?"
    };

    private final String[] SELECT_JOBS_LIMIT_LIKE_BY_DATE_RANGE = new String[] {
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_CREATED >= ? AND JOB_CREATED < ? ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_CREATED >= ? AND JOB_CREATED < ? AND JOB_NAME LIKE '%?%' ORDER BY JOB_CREATED DESC LIMIT ?, ?",
            "SELECT * FROM {0}JOB_DETAILS WHERE JOB_CREATED >= ? AND JOB_CREATED < ? AND JOB_GROUP LIKE '%?%' ORDER BY JOB_CREATED DESC LIMIT ?, ?"
    };

    public List<WinderJobDetail> selectJobs(JobFilter filter) throws WinderScheduleException {

        List<WinderJobDetail> jobs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;

        CascadingClassLoadHelper cascadingClassLoadHelper = new CascadingClassLoadHelper();
        cascadingClassLoadHelper.initialize();
        ResultSet rs = null;
        try {
            connection = DBConnectionManager.getInstance().getConnection(dataSourceName);

            // start making queries
            boolean hasDateRange = false;

            Date start = filter.getStart();
            Timestamp startTime = null, endTime = null;
            if (start != null) {
                hasDateRange = true;

                startTime = new Timestamp(start.getTime());

                endTime = (filter.getEnd() == null) ? new Timestamp(System.currentTimeMillis())
                        : new Timestamp(filter.getEnd().getTime());
            }

            String[] sqls = null;
            if (filter.isLike()) {
                sqls = hasDateRange ? SELECT_JOBS_LIMIT_LIKE_BY_DATE_RANGE : SELECT_JOBS_LIMIT_LIKE;
            }
            else {
                sqls = hasDateRange ? SELECT_JOBS_LIMIT_BY_DATE_RANGE : SELECT_JOBS_LIMIT;
            }

            JobKeyField keyField = filter.getKeyField();
            ps = connection.prepareStatement(sqls[keyField.ordinal()]);

            if (hasDateRange) {
                if (keyField == JobKeyField.ALL) {
                    ps.setTimestamp(1, startTime);
                    ps.setTimestamp(2, endTime);
                    ps.setInt(3, filter.getOffset());
                    ps.setInt(4, filter.getLimit());
                }
                else {
                    if (filter.isLike()) {
                        ps.setTimestamp(1, startTime);
                        ps.setTimestamp(2, endTime);
                        ps.setString(3, filter.getValue());
                        ps.setInt(4, filter.getOffset());
                        ps.setInt(5, filter.getLimit());
                    }
                    else {
                        ps.setString(1, filter.getValue());
                        ps.setTimestamp(2, startTime);
                        ps.setTimestamp(3, endTime);
                        ps.setInt(4, filter.getOffset());
                        ps.setInt(5, filter.getLimit());
                    }
                }
            }
            else {
                if (keyField == JobKeyField.ALL) {
                    ps.setInt(1, filter.getOffset());
                    ps.setInt(2, filter.getLimit());
                }
                else {
                    ps.setString(1, filter.getValue());
                    ps.setInt(2, filter.getOffset());
                    ps.setInt(3, filter.getLimit());
                }
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                QuartzJobDetail jobDetail = makeJobDetail(cascadingClassLoadHelper, rs);
                jobs.add(jobDetail);
            }
        } catch (Exception e) {
            throw new WinderScheduleException("Job listing failed", e);
        } finally {
            close(rs);
            close(ps);
            close(connection);
        }
        return jobs;
    }

    private QuartzJobDetail makeJobDetail(CascadingClassLoadHelper cascadingClassLoadHelper, ResultSet rs) throws SQLException, ClassNotFoundException, IOException {
        JobDetailImpl jobDetail = new JobDetailImpl();

        String groupName = rs.getString(Constants.COL_JOB_GROUP);
        String jobName = rs.getString(Constants.COL_JOB_NAME);
        jobDetail.setName(jobName);
        jobDetail.setGroup(groupName);
        jobDetail.setDescription(rs.getString(Constants.COL_DESCRIPTION));
        jobDetail.setJobClass(
                cascadingClassLoadHelper.loadClass(rs.getString(Constants.COL_JOB_CLASS), Job.class)
        );
        jobDetail.setDurability(rs.getBoolean(Constants.COL_IS_DURABLE));
        jobDetail.setRequestsRecovery(rs.getBoolean(Constants.COL_REQUESTS_RECOVERY));

        Map<?, ?> map = (Map<?, ?>) getObjectFromBlob(rs, COL_JOB_DATAMAP);

        if (map != null) {
            jobDetail.setJobDataMap(new JobDataMap(map));
        }

        JobId jobId = new QuartzJobId(groupName, jobName, engine.getClusterName());

        QuartzJobDetail quartzJobDetail = new QuartzJobDetail(engine, jobId, jobDetail,
                rs.getTimestamp(WinderJDBCDelegate.COL_JOB_CREATED));
        return quartzJobDetail;
    }

    private Object getObjectFromBlob(ResultSet rs, String colName)
            throws ClassNotFoundException, IOException, SQLException {
        Object obj = null;

        Blob blobLocator = rs.getBlob(colName);
        if (blobLocator != null && blobLocator.length() != 0) {
            InputStream binaryInput = blobLocator.getBinaryStream();

            if (null != binaryInput) {
                if (binaryInput instanceof ByteArrayInputStream
                        && ((ByteArrayInputStream) binaryInput).available() == 0 ) {
                    //do nothing
                } else {
                    ObjectInputStream in = new ObjectInputStream(binaryInput);
                    try {
                        obj = in.readObject();
                    } finally {
                        in.close();
                    }
                }
            }

        }
        return obj;
    }


    private static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("Error when closing Result Set", e);
            }
        }
    }

    private static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.warn("Error when closing PreparedStatement", e);
            }
        }
    }

    private static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.warn("Error when closing the JDBC connection", e);
            }
        }
    }

    private List<WinderJobDetail> limit(List<WinderJobDetail> jobs, JobFilter filter) {
        int skip = filter.getOffset();
        int limit = filter.getLimit();

        List<WinderJobDetail> result = new ArrayList<>();

        int size = jobs.size();
        skip = skip < 0 ? 0 : skip > size ? size : skip;

        for (int i = 0; i < size; i++) {
            if (i < skip) {
                continue;
            }

            if (limit > 0 && result.size() == limit) {
                break;
            }

            result.add(jobs.get(i));
        }

        return result;
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
            d.setEndTime(new Date());
        }
        updateJobDetail(d);
    }

    public void start() {
        if (quartzScheduler != null) {
            try {
                quartzScheduler.start();
            } catch (SchedulerException e) {
                log.warn("Starting quartz exception", e);
            }
        }
    }

    public void stop() {
        if (quartzScheduler != null) {
            try {
                quartzScheduler.shutdown(true);
            } catch (SchedulerException e) {
                log.warn("Stopping quartz exception", e);
            }
        }
    }
}
