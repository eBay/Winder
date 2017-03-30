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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

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
    private Parameters<Object> data;

    private TI taskInput;
    private TR taskResult;

    private static Logger log = LoggerFactory.getLogger(QuartzJobSummary.class);

    private final int maxStack;

    public QuartzJobSummary(WinderEngine engine, JobId jobId, Parameters<Object> data) {
        this.engine = engine;
        this.jobId = jobId;
        this.data = data;
        data.put(KEY_JOB_ID, jobId.toString());
        this.maxStack = engine.getConfiguration().getInt("winder.job.maxStack", 512);
    }

    void setParentJobId(QuartzJobId parentId) {
        data.put(KEY_JOB_PARENT, parentId.toString());
    }

    @Override
    public JobId getParentJobId() {
        String parentIdStr = data.getString(KEY_JOB_PARENT);
        if (parentIdStr == null) {
            return null;
        }
        return WinderUtil.toJobId(parentIdStr, jobId.getCluster());
    }

    void setParentJobId(JobId parentJobId) {
        data.put(KEY_JOB_PARENT, parentJobId.toString());
    }


    @Override
    public JobId[] getChildJobIds() {
        List<String> ids = data.getStringList(KEY_CHILD_JOBS);
        if (ids == null || ids.size() == 0) {
            return new JobId[0];
        }

        JobId[] result = new JobId[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = WinderUtil.toJobId(ids.get(i), jobId.getCluster());
        }
        return result;
    }

    @Override
    public List<UserAction> getUserActions() {
        List<Map> list = getListOfMap(KEY_USER_ACTIONS);
        List<UserAction> userActions = new ArrayList<>(list.size());
        for(Map m: list) {
            userActions.add(new QuartzUserAction(engine, new ParametersMap<>(m)));
        }
        return userActions;
    }

    protected List<Map> getListOfMap(String key) {
        List<Map> list = data.getList(key);
        if (list == null) {
            list = new ArrayList<>(5);
            data.put(key, list);
        }
        return list;
    }

    @Override
    public UserAction addUserAction(UserActionType type, String message, String owner) {
        QuartzUserAction userAction = new QuartzUserAction(engine, new ParametersMap<>(),
                type, message, owner);
        addUserAction(userAction);
        return userAction;
    }

    /**
     * Add new owner action
     */
    public void addUserAction(UserAction userAction) {
        getListOfMap(KEY_USER_ACTIONS).add(userAction.toParameters().toMap());
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum executionStatus, String statusMessage) {
        QuartzStatusUpdate update = new QuartzStatusUpdate(engine, new ParametersMap<>(),
                executionStatus, statusMessage);

        getListOfMap(KEY_STATUS_UPDATES).add(update.toParameters().toMap());
        return update;
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
        List<Map> list = getListOfMap(KEY_STATUS_UPDATES);
        List<StatusUpdate> statusUpdates = new ArrayList<>(list.size());
        for(Map m: list) {
            statusUpdates.add(new QuartzStatusUpdate(engine, new ParametersMap<>(m)));
        }
        return statusUpdates;
    }

    /**
     * Add new child job id
     *
     * @param jobId
     */
    public void addChildJobIds(JobId jobId) {
        List<String> ids = data.getStringList(KEY_CHILD_JOBS);

        if (ids == null) {
            ids = new ArrayList<>(1);
        } else {
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
        data.put(KEY_CHILD_JOBS, ids);
    }

    @Override
    public String getTarget() {
        return data.getString(KEY_JOB_TARGET);
    }

    @Override
    public void setTarget(String target) {
        data.put(KEY_JOB_TARGET, target);
    }

    @Override
    public String getAction() {
        return data.getString(KEY_JOB_ACTION);
    }

    @Override
    public void setAction(String action) {
        data.put(KEY_JOB_ACTION, action);
    }

    @Override
    public TR getTaskResult() {
        if (taskResult == null) {
            Map<String, Object> result = (Map<String, Object>)data.get(KEY_JOB_RESULT);
            if (result != null) {
                taskResult = (TR) new WinderTaskResult(result);
            }
            else {
                setTaskResult((TR)new WinderTaskResult());
            }
        }
        return taskResult;
    }

    @Override
    public void setTaskResult(TR result) {
        this.taskResult = result;
        data.put(KEY_JOB_RESULT, result.toMap());
    }

    @Override
    public TI getTaskInput() {
        if (taskInput == null) {
            Map<String, Object> input = (Map<String, Object>)data.get(KEY_JOB_INPUT);
            String jobClass = data.getString(KEY_JOB_CLASS);
            try {
                WinderTaskInput ti = new WinderTaskInput(input != null ? input : new HashMap<String, Object>());
                ti.setJobOwner(getOwner());
                ti.setJobClass(Class.forName(jobClass));
                taskInput = (TI)ti;
            }
            catch (ClassNotFoundException e) {
                log.error("Job class not found:" + jobClass, e);
            }
        }
        return taskInput;
    }

    @Override
    public void setTaskInput(TI taskInput) {
        this.taskInput = taskInput;
        data.put(KEY_JOB_INPUT, taskInput.toMap());
    }

    @Override
    public String getOwner() {
        return data.getString(KEY_JOB_OWNER);
    }

    @Override
    public TaskData addTask(String taskId, String taskName) {
        List<Map> list = getListOfMap(KEY_TASKS);
        QuartzData statusData = new QuartzData(engine);
        statusData.setId(taskId);
        statusData.setName(taskName);
        statusData.setDateCreated(new Date());
        list.add(statusData.toMap());
        return statusData;
    }

    @Override
    public List<TaskData> getAllTasks() {
        List<Map> list = getListOfMap(KEY_TASKS);
        List<TaskData> statusDatas = new ArrayList<>(list.size());
        for(Map m: list) {
            statusDatas.add(new QuartzData(engine, m));
        }
        return statusDatas;
    }

    @Override
    public Map<String, Object> toMap() {
        return data.toMap();
    }
}
