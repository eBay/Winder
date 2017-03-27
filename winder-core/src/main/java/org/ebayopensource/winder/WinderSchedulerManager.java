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

    /**
     * Return job detail by jobId
     *
     * @param jobId Job Id
     * @return Job Detail
     */
    WinderJobDetail getJobDetail(String jobId) throws WinderScheduleException;

    // remove all related triggers
    void unscheduleJob(JobId jobId) throws WinderScheduleException;

    // Update the JOB_ID field if using SQL for fast search of jobs xxx
    void updateJobData(WinderJobDetail job) throws WinderScheduleException;

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

    /**
     * List job details by filter
     *
     * @param filter List Job filter
     * @return List for Job Details
     * @throws WinderScheduleException
     */
    List<WinderJobDetail> listJobDetails(JobFilter filter) throws WinderScheduleException;

}
