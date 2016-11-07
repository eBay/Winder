package org.ebayopensource.winder;

import java.util.Date;
import java.util.List;

/**
 * Scheduler Manager
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderSchedulerManager<TI extends TaskInput> {

    /**
     * Return job detail by jobId
     *
     * @param jobId Job Id
     * @return Job Detail
     */
    WinderJobDetail getJobDetail(JobId jobId) throws WinderScheduleException;

    // remove all related triggers
    void unscheduleJob(JobId jobId) throws WinderScheduleException;

    // Update the JOB_ID field if using SQL for fast search of jobs xxx
    void updateJobData(WinderJobDetail job);

    JobId scheduleChildJob(TI input, WinderJobContext parentJobCtx) throws WinderScheduleException;

    boolean doneYet(List<WinderJobDetail> childJobDetails);

    JobId scheduleJob(TI input) throws WinderScheduleException;

    void rescheduleJob(JobId jobId, Date jobStartTime, int stageIntervalSec, int maxTimeForJobSec) throws WinderScheduleException;

    void rescheduleCronJob(JobId jobId, Date jobStartTime, String cronExpression) throws WinderScheduleException;

    JobId scheduleCronJob(TI input, String cronExpression) throws WinderScheduleException;

    void pauseJob(JobId jobId) throws WinderScheduleException;

    void resumeJob(JobId jobId, StatusEnum newStatus, String message, Boolean autoPause, String user) throws WinderScheduleException;

    void cancelJob(JobId jobId, boolean force) throws WinderScheduleException;

    void markCancelInProgress(JobId jobId, String message, String user) throws WinderScheduleException;

    boolean successChildJob(List<WinderJobDetail> childJobStatusDetails);
}
