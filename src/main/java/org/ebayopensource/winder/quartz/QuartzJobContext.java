package org.ebayopensource.winder.quartz;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.ebayopensource.winder.*;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.impl.triggers.AbstractTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

public class QuartzJobContext implements WinderJobContext {

    private final JobExecutionContext quartzCtx;
    private final QuartzJobDetail jobDetail;
    private final WinderJobSummary summary;
    private final QuartzJobId jobId;
    private Trigger trigger;

    private WinderSchedulerManager scheduler;

    private static Logger log = LoggerFactory.getLogger(QuartzJobContext.class);

    private WinderEngine winderEngine;

    public QuartzJobContext(WinderEngine winderEngine, JobExecutionContext qctx) {
        this.winderEngine = winderEngine;
        this.scheduler = winderEngine.getSchedulerManager();
        quartzCtx = qctx;

        JobDetail jd = quartzCtx.getJobDetail();
        jobId = new QuartzJobId(jd.getKey(), winderEngine.getClusterName());
        jobDetail = new QuartzJobDetail(winderEngine, jobId, jd);
        summary = jobDetail.getSummary();

        trigger = quartzCtx.getTrigger();
        if (trigger == null) {
            throw new IllegalStateException("missing trigger");
        }
    }

    public WinderJobDetail getJobDetail() {
        return jobDetail;
    }

    @Override
    public WinderEngine getEngine() {
        return winderEngine;
    }

    public QuartzJobId getJobId() {
        return jobId;
    }

    public String getJobIdAsString() {
        return getJobId().toString();
    }

    @Override
    public int getJobStep() {
        return 0;
    }

    @Override
    public void setJobStep(int step) {

    }


    public boolean isRecovering() {
        return quartzCtx.isRecovering();
    }

    public String getStatusMessage() {
        String result = jobDetail.getJobDataMap().getString(KEY_JOBSTATUSMSG);
        return (result == null) ? "" : result;
    }

    public void setStatusMessage(String msg) {
        String nmsg = (msg == null) ? "" : msg;
        jobDetail.getJobDataMap().put(KEY_JOBSTATUSMSG, nmsg);
    }


    public void setStatusMessage(String msg, Throwable t) {
        if (t != null) {
            StringWriter writer = new StringWriter();
            if (msg != null) {
                writer.write(msg);
                writer.append("\r\n");
            }
            t.printStackTrace(new PrintWriter(writer));
            setStatusMessage(writer.toString());
        }
        else {
            setStatusMessage(msg);
        }
    }


    public boolean isAwaitingForAction(boolean isAwaiting) {
        return jobDetail.getJobDataMap().getBoolean(KEY_JOB_IS_AWAITING_FOR_ACTION);
    }

    public void setAwaitingForAction(boolean isAwaiting) {
        jobDetail.getJobDataMap().put(KEY_JOB_IS_AWAITING_FOR_ACTION, isAwaiting);
    }

    public void setComplete() {
        markJobCompleteAndUnschedule(StatusEnum.COMPLETED);
    }

    public void setError() {
        markJobCompleteAndUnschedule(StatusEnum.ERROR);
    }

    public void setCompleteWithWarning() {
        markJobCompleteAndUnschedule(StatusEnum.WARNING);
    }

    private void markJobCompleteAndUnschedule(StatusEnum status) {
        jobDetail.getJobDataMap().put(KEY_JOBSTATUS, String.valueOf(status));
        Date endDate = new Date();
        jobDetail.setEndDate(winderEngine.formatDate(endDate));

        if (log.isDebugEnabled()) {
            log.debug("Job complete: " + jobId.toString());
        }

        //stop the firing
        if (trigger instanceof AbstractTrigger) {
            AbstractTrigger abstractTrigger = (AbstractTrigger) trigger;
            abstractTrigger.setEndTime(endDate);
            abstractTrigger.setNextFireTime(null);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Unexpected trigger type, not AbstractTrigger.");
            }
        }
        try {
            scheduler.unscheduleJob(jobId);
        } catch (WinderException e) {
            log.error("Failure unscheduling job: " + jobId.toString(), e);
        }
    }

    public StatusEnum getJobStatus() {
        String statusName = jobDetail.getJobDataMap().getString(KEY_JOBSTATUS);
        if (statusName == null) {
            return StatusEnum.UNKNOWN;
        }
        StatusEnum result;
        try {
            result = StatusEnum.valueOf(statusName);
        } catch (Exception e) {
            return StatusEnum.UNKNOWN;
        }
        return result;
    }

    public void setJobStatus(StatusEnum status) {
        jobDetail.getJobDataMap().put(KEY_JOBSTATUS,status.name());
    }


    public WinderJobSummary getJobStateData() {
        return summary;
    }

    public JobId[] getChildJobs() {
        return summary.getChildJobIds();
    }

//    public JobKey getJobKey() {
//        return jobDetail.getKey();
//    }

    public void updateJobData() {
        scheduler.updateJobData(jobDetail);
    }
}
