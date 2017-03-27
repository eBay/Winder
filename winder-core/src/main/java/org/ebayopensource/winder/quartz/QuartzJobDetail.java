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

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.ebayopensource.winder.*;
import org.ebayopensource.winder.util.JsonUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private Parameters<Object> dataAsParameters;

    private QuartzJobSummary<TI, TR> jobSummary;

    private WinderEngine engine;

    private Date created;

    public QuartzJobDetail(WinderEngine engine, JobId jobId, JobDetail jobDetail) {
        this(engine, jobId, jobDetail, null);
    }


    public QuartzJobDetail(WinderEngine engine, JobId jobId, JobDetail jobDetail, Date dateCrated) {
        this.engine = engine;
        this.jobId = jobId;
        this.jobDetail = jobDetail;
        this.jobDataMap = jobDetail.getJobDataMap();
        this.dataAsParameters = new ParametersMap<>(jobDataMap);
        this.created = dateCrated != null ? dateCrated : new Date();
        this.jobSummary = new QuartzJobSummary<>(engine, jobId, dataAsParameters);
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
        return dataAsParameters.getDate(KEY_JOB_START_DATE);
    }

    @Override
    public Date getEndTime() {
        return dataAsParameters.getDate(KEY_JOB_END_DATE);
    }

    @Override
    public void setStartTime(Date date) {
        dataAsParameters.put(KEY_JOB_START_DATE, date.getTime());
    }

    @Override
    public void setEndTime(Date date) {
        dataAsParameters.put(KEY_JOB_END_DATE, date.getTime());
    }

    @Override
    public StatusEnum getStatus() {
        return dataAsParameters.getEnum(StatusEnum.class, KEY_JOB_STATUS, StatusEnum.UNKNOWN);
    }

    @Override
    public void setStatus(StatusEnum status) {
        if (status == null) {
            status = StatusEnum.UNKNOWN;
        }
        dataAsParameters.put(KEY_JOB_STATUS, status.name());
    }

    @Override
    public TI getInput() {
        return jobSummary.getTaskInput();
    }

    @Override
    public TR getResult() {
        return jobSummary.getTaskResult();
    }

    @Override
    public Parameters<Object> getDataParameters() {
        return dataAsParameters;
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
    public void addUserAction(UserAction userAction) {
        jobSummary.addUserAction(userAction);
    }

    @Override
    public boolean isAwaitingForAction() {
        return dataAsParameters.getBoolean(KEY_JOB_IS_AWAITING_FOR_ACTION);
    }

    @Override
    public void setAwaitingForAction(boolean awaitingForAction) {
        dataAsParameters.put(KEY_JOB_IS_AWAITING_FOR_ACTION, awaitingForAction);
    }

    @Override
    public List<StatusUpdate> getUpdates() {
        return jobSummary.getUpdates();
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum status, String message) {
        return jobSummary.addUpdate(status, message);
    }

    @Override
    public Map<String, Object> toMap() {
        return dataAsParameters.toMap();
    }

    private static Logger log = LoggerFactory.getLogger(QuartzJobDetail.class);

    @Override
    public String toJson() {
        try {
            return JsonUtil.writeValueAsString(dataAsParameters);
        } catch (IOException e) {
            log.warn("Convert to json exception", e);
            throw new IllegalStateException("Illegal state");
        }
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
