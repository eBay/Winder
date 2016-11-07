package org.ebayopensource.winder.quartz;

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.winder.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String jobCategory;

    private WinderEngine engine;

    private static Logger log = LoggerFactory.getLogger(QuartzJobDetail.class);

    public QuartzJobDetail(WinderEngine engine, JobId jobId, JobDetail jobDetail) {
        this.engine = engine;
        this.jobId = jobId;
        this.jobDetail = jobDetail;
        this.jobDataMap = jobDetail.getJobDataMap();
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
    public String getEndDate() {
        return jobDataMap.getString(KEY_JOBENDDATE);
    }

    @Override
    public void setEndDate(String date) {
        jobDataMap.put(KEY_JOBENDDATE, date);
    }

    @Override
    public StatusEnum getStatus() {
        String value = jobDataMap.getString(KEY_JOBSTATUS);
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
        jobDataMap.put(KEY_JOBSTATUS, status.name());
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
        jobCategory = taskInput.getJobCategory();
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

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }
}
