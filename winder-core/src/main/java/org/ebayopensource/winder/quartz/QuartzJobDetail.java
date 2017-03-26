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

import org.ebayopensource.winder.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * Job Detail
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class QuartzJobDetail<TI extends TaskInput, TR extends TaskResult> implements WinderJobDetail<TI, TR>, JobDetail {

    private JobId jobId;

    private JobDetail jobDetail;

    private JobDataMap jobDataMap;

    private QuartzJobSummary<TI, TR> jobSummary;

    private WinderEngine engine;

    private Date created;

//    private static Logger log = LoggerFactory.getLogger(QuartzJobDetail.class);

    public QuartzJobDetail(WinderEngine engine, JobId jobId, JobDetail jobDetail) {
        this(engine, jobId, jobDetail, null);
    }


    public QuartzJobDetail(WinderEngine engine, JobId jobId, JobDetail jobDetail, Date dateCrated) {
        this.engine = engine;
        this.jobId = jobId;
        this.jobDetail = jobDetail;
        this.jobDataMap = jobDetail.getJobDataMap();
        this.created = dateCrated != null ? dateCrated : new Date();
        this.jobSummary = new QuartzJobSummary<>(engine, jobId, jobDataMap);
    }

    public WinderJobSummary getSummary() {
        return jobSummary;
    }

    @Override
    public JobId getJobId() {
        return jobId;
    }

    @Override
    public JobId getParentJobId() {
        return jobSummary.getParentJobId();
    }

    void setParentJobId(JobId parentJobId) {
        jobSummary.setParentJobId(parentJobId);
    }

    @Override
    public JobId[] getChildJobIds() {
        return jobSummary.getChildJobIds();
    }

    /**
     * Add new child job id
     *
     * @param jobId
     */
    public void addChildJobIds(JobId jobId) {
        jobSummary.addChildJobIds(jobId);
    }

    @Override
    public void setChildJobIds(JobId[] jobIds) {
        jobSummary.setChildJobIds(jobIds);
    }

    @Override
    public JobKey getKey() {
        return jobDetail.getKey();
    }

    @Override
    public String getDescription() {
        return jobDetail.getDescription();
    }

    @Override
    public Class<? extends Job> getJobClass() {
        return jobDetail.getJobClass();
    }

    @Override
    public boolean isDurable() {
        return jobDetail.isDurable();
    }

    @Override
    public boolean isPersistJobDataAfterExecution() {
        return jobDetail.isPersistJobDataAfterExecution();
    }

    @Override
    public boolean isConcurrentExectionDisallowed() {
        return jobDetail.isConcurrentExectionDisallowed();
    }

    @Override
    public boolean requestsRecovery() {
        return jobDetail.requestsRecovery();
    }

    @Override
    public JobBuilder getJobBuilder() {
        return jobDetail.getJobBuilder();
    }

    @Override
    public boolean isAutoPause() {
        return jobSummary.getTaskInput().getBoolean(KEY_AUTO_PAUSE, true);
    }

    @Override
    public void setAutoPause(boolean autoPause) {
        jobSummary.getTaskInput().put(KEY_AUTO_PAUSE, autoPause);
        jobSummary.setTaskInput(jobSummary.getTaskInput());
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getStartTime() {
        long l = jobDataMap.getLong(KEY_JOB_START_DATE);
        return l > 0 ? new Date(l) : null;
    }

    @Override
    public Date getEndTime() {
        long l = jobDataMap.getLong(KEY_JOB_END_DATE);
        return l > 0 ? new Date(l) : null;
    }

    @Override
    public void setStartTime(Date date) {
        jobDataMap.put(KEY_JOB_START_DATE, date.getTime());
    }

    @Override
    public void setEndTime(Date date) {
        jobDataMap.put(KEY_JOB_END_DATE, date.getTime());
    }

    @Override
    public StatusEnum getStatus() {
        String value = jobDataMap.getString(KEY_JOB_STATUS);
        if (value != null) {
            try {
                return StatusEnum.valueOf(value.toUpperCase());
            } catch (Exception ex) {
            }
        }
        return StatusEnum.UNKNOWN;
    }

    @Override
    public void setStatus(StatusEnum status) {
        if (status == null) {
            status = StatusEnum.UNKNOWN;
        }
        jobDataMap.put(KEY_JOB_STATUS, status.name());
    }

    @Override
    public TI getInput() {
        return jobSummary.getTaskInput();
    }

    @Override
    public TR getResult() {
        return jobSummary.getTaskResult();
    }

    void setResult(TR result) {
        jobSummary.setTaskResult(result);
    }

    void setInput(TI taskInput) {
        jobSummary.setTaskInput(taskInput);
    }

    @Override
    public List<UserAction> getUserActions() {
        return jobSummary.getUserActions();
    }

    @Override
    public UserAction addUserAction(UserActionType type, String message, String owner) {
        return jobSummary.addUserAction(type, message, owner);
    }

    @Override
    public UserAction addUserAction(UserAction userAction) {
        return QuartzJobUtil.addUserAction(engine, jobDataMap, userAction);
    }

    @Override
    public boolean isAwaitingForAction() {
        return jobDataMap.getBoolean(KEY_JOB_IS_AWAITING_FOR_ACTION);
    }

    @Override
    public void setAwaitingForAction(boolean awaitingForAction) {
        jobDataMap.put(KEY_JOB_IS_AWAITING_FOR_ACTION, awaitingForAction);
    }

    @Override
    public List<StatusUpdate> getUpdates() {
        return jobSummary.getUpdates();
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum status, String message) {
        return jobSummary.addUpdate(status, message);
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public JobDataMap getJobDataMap() {
        return jobDetail.getJobDataMap();
    }


    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int compareTo(WinderJobDetail o) {
        if (o == null) {
            return 0;
        }
        long first = getCreated().getTime();
        long second = o.getCreated().getTime();

        if (first == second) {
            return 0;
        }
        return first > second ? -1 : 1;
    }
}
