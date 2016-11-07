package org.ebayopensource.winder.quartz;

import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.winder.*;
import org.ebayopensource.winder.util.JsonUtil;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * Job State Data
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class QuartzJobSummary<TI extends TaskInput, TR extends TaskResult> implements WinderJobSummary<TI, TR> {

    private WinderEngine engine;
    private JobId jobId;
    private JobDataMap jobDataMap;

    private TI taskInput;
    private TR taskResult;

    private static Logger log = LoggerFactory.getLogger(QuartzJobSummary.class);

    private final int maxStack;

    public QuartzJobSummary(WinderEngine engine, JobId jobId, JobDataMap jobDataMap) {
        this.engine = engine;
        this.jobId = jobId;
        this.jobDataMap = jobDataMap;
        this.maxStack = engine.getConfiguration().getInt("winder.job.maxStack", 512);
    }

    void setParentJobId(QuartzJobId parentId) {
        jobDataMap.put(KEY_JOBPARENT, parentId.toString());
    }

    @Override
    public JobId getParentJobId() {
        String parentIdStr = jobDataMap.getString(KEY_JOBPARENT);
        if (parentIdStr == null) {
            return null;
        }
        return QuartzJobId.createFromString(parentIdStr, jobId.getCluster());
    }

    void setParentJobId(JobId parentJobId) {
        jobDataMap.put(KEY_JOBPARENT, parentJobId.toString());
    }


    @Override
    public JobId[] getChildJobIds() {
        String childText = jobDataMap.getString(KEY_CHILDJOBS);
        if (childText == null) {
            return new JobId[0];
        }

        String[] ids;
        try {
            ids = JsonUtil.readValue(childText, String[].class);
        } catch (IOException e) {
            log.warn("Invalid child jobs ids:" + childText, e);
            return new JobId[0];
        }
        JobId[] result = new JobId[ids.length];
        for (int i = 0; i < ids.length; i++) {
            result[i] = QuartzJobId.createFromString(ids[i], jobId.getCluster());
        }
        return result;
    }

    @Override
    public List<UserAction> getUserActions() {
        return QuartzJobUtil.getAllStatus(QuartzStatusUpdate.class, engine, JOB_ALERT_STATUS_PREFIX, jobDataMap);
    }

    @Override
    public UserAction addUserAction(UserActionType type, String message, String owner) {
        return QuartzJobUtil.addUserAction(engine, jobDataMap, type, message, owner);
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum executionStatus, String statusMessage) {
        return QuartzJobUtil.addOrGetUpdate(engine, JOBSTATUSUPDATE_PREFIX, jobDataMap, executionStatus, statusMessage);
    }

    public StatusUpdate addUpdate(StatusEnum status, String message, Throwable ex) {

        if ((ex != null) && !(ex instanceof IllegalArgumentException)) {
            StringBuilder buf = new StringBuilder();
            buf.append(message);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            StringBuffer tmp = sw.getBuffer();
            pw.print('\n');
            // start stack trace on its own line
            ex.printStackTrace(pw); // NOSONAR
            buf.append(" **** ");
            buf.append(QuartzJobUtil.formatString(tmp.toString(), maxStack, true));
            message = buf.toString();
        }
        return addUpdate(status, message);
    }

    @Override
    public List<StatusUpdate> getUpdates() {
        return QuartzJobUtil.getAllStatus(QuartzStatusUpdate.class, engine, JOBSTATUSUPDATE_PREFIX, jobDataMap);
    }

    /**
     * Add new child job id
     *
     * @param jobId
     */
    public void addChildJobIds(JobId jobId) {
        String childText = jobDataMap.getString(KEY_CHILDJOBS);

        List<String> ids;

        if (childText == null) {
            ids = new ArrayList<>(1);
        } else {
            try {
                ids = JsonUtil.readValue(childText, ArrayList.class);
            } catch (IOException e) {
                log.warn("Convert json to array error:" + childText, e);
                ids = new ArrayList<>(1);
            }
            int overflow = ids.size() - MAX_CHILD_JOB_SIZE;
            if (overflow >= 0) {
                for (int i = overflow; i >= 0; i--) {
                    ids.remove(i);
                }
            }
        }

        ids.add(jobId.toString());

        setChildJobIds(ids);
    }

    public void setChildJobIds(JobId[] jobIds) {
        List<String> ids = new ArrayList<>(jobIds.length);
        for(JobId jobId : jobIds) {
            ids.add(jobId.toString());
        }
        setChildJobIds(ids);
    }

    private void setChildJobIds(List<String> ids) {
        try {
            jobDataMap.put(KEY_CHILDJOBS, JsonUtil.writeValueAsString(ids));
        } catch (IOException e) {
            log.warn("Convert object to json error:" + ids, e);
        }
    }

    @Override
    public String getTarget() {
        return jobDataMap.getString(KEY_JOBTARGET);
    }

    @Override
    public void setTarget(String target) {
        jobDataMap.put(KEY_JOBTARGET, target);
    }

    @Override
    public String getAction() {
        return jobDataMap.getString(KEY_JOBACTION);
    }

    @Override
    public void setAction(String action) {
        jobDataMap.put(KEY_JOBACTION, action);
    }

    @Override
    public TR getTaskResult() {
        return taskResult;
    }

    @Override
    public void setTaskResult(TR result) {
        this.taskResult = result;
        jobDataMap.put(KEY_JOBRESULT, result.toJson());
    }

    @Override
    public TI getTaskInput() {
        return taskInput;
    }

    @Override
    public void setTaskInput(TI taskInput) {
        this.taskInput = taskInput;
        jobDataMap.put(KEY_JOBINPUT, taskInput.toJson());
    }

    @Override
    public String getOwner() {
        return jobDataMap.getString(KEY_JOBOWNER);
    }

    private List<String> getTaskIds() {
        String text = jobDataMap.getString(KEY_TASKS);
        if (!StringUtils.isBlank(text)) {
            try {
                return JsonUtil.readValue(text, ArrayList.class);
            } catch (IOException e) {
            }
        }
        return new ArrayList<>(1);
    }
    @Override
    public TaskStatusData addTaskStatus(String taskId, String taskName) {
        List<String> taskIds = getTaskIds();
        taskIds.add(taskId);
        try {
            jobDataMap.put(KEY_TASKS, JsonUtil.writeValueAsString(taskIds));
        } catch (IOException e) {
            throw new IllegalStateException("Json exception" + taskIds);
        }

        QuartzStatusData statusData = new QuartzStatusData(engine, taskId, jobDataMap);
        statusData.setName(taskName);
        statusData.setDateCreated(new Date());
        return statusData;
    }

    @Override
    public TaskStatusData getTaskStatus(String taskId) {
        List<String> taskIds = getTaskIds();
        if (!taskIds.contains(taskId)) {
            return null;
        }
        return new QuartzStatusData(engine, taskId, jobDataMap);
    }

    @Override
    public List<TaskStatusData> getAllTaskStatuses() {
        List<String> taskIds = getTaskIds();
        List<TaskStatusData> result = new ArrayList<>(taskIds.size());

        for (String taskId : taskIds) {
            result.add(new QuartzStatusData(engine, taskId, jobDataMap));
        }
        return result;
    }
}
