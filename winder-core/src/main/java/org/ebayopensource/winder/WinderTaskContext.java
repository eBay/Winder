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

import org.ebayopensource.common.config.PropertyUtil;
import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.winder.metadata.JobMetadata;
import org.ebayopensource.winder.metadata.StepMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Winder Task Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class WinderTaskContext<TI extends TaskInput, TR extends TaskResult> implements TaskContext<TI, TR> {


    private boolean forceBreak = false;

    protected WinderJobContext jobContext;

    protected WinderEngine engine;

    private static Logger log = LoggerFactory.getLogger(WinderTaskContext.class);

    protected TI taskInput;

    protected TR taskResult;

    protected Class<? extends Step> jobClass;

    private StatusEnum jobStatus;

    protected WinderJobDetail jobDetail;

    private JobMetadata jobMetadata;

    private StepMetadata stepMetadata;

    public WinderTaskContext(WinderJobContext jobContext, Class<? extends Step> jobClass) throws WinderScheduleException {
        this.jobClass = jobClass;
        this.jobContext = jobContext;
        this.engine = jobContext.getEngine();

        init();
    }

    protected void init() throws WinderScheduleException {
        WinderSchedulerManager schedulerManager = engine.getSchedulerManager();
        jobDetail = schedulerManager.getJobDetail(jobContext.getJobId());

        jobMetadata = engine.getStepRegistry().getMetadata(jobClass);

        taskInput = initInput(jobDetail.getInput());
        taskResult = initResult(jobDetail.getResult());
    }

    protected TI initInput(Parameters<Object> input) {
        if (input instanceof TaskInput) {
            return (TI)input;
        }
        else {
            return (TI) new WinderTaskInput(input);
        }
    }

    protected TR initResult(Parameters<Object> result) {
        if (result == null) {
            return (TR)new WinderTaskResult();
        }
        if (result instanceof TaskInput) {
            return (TR)result;
        }
        else {
            return (TR) new WinderTaskResult(result);
        }
    }

    public boolean isForceBreak() {
        return forceBreak;
    }

    public void setForceBreak(boolean forceBreak) {
        this.forceBreak = forceBreak;
    }

    @Override
    public StatusEnum getJobStatus() {
        return jobStatus;
    }

    private int groupId = 1;

    public int getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public JobId getJobId() {
        return jobContext.getJobId();
    }

    @Override
    public String getJobIdAsString() {
        return jobContext.getJobIdAsString();
    }

    @Override
    public TI getTaskInput() {
        return taskInput;
    }

    @Override
    public TR getTaskResult() {
        return taskResult;
    }

    @Override
    public StepMetadata getStepMetadata() {
        return stepMetadata;
    }

    @Override
    public JobMetadata getJobMetadata() {
        return jobMetadata;
    }

    public final void setCurrentStep(Step step) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Job {%s} status is: {%s} nextStep is {%s}", getJobId(),
                    getJobStatus(), step));
        }
        stepMetadata = jobMetadata.getStep(step.code());
        jobContext.setJobStep(step.code());
    }

    @Override
    public Step<TI, TR, ? extends WinderTaskContext> getCurrentStep() {
        int jobStep = jobContext.getJobStep();
        stepMetadata = jobMetadata.getStep(jobStep);
        if (log.isDebugEnabled()) {
            log.debug(String.format("Job {%s} execution status {%s} step {%s}", getJobId(), getJobStatus(), stepMetadata.getName()));
        }
        return stepMetadata.toStep();
    }

    private List<StepMetadata> doneSteps = null;

    @Override
    public boolean isDone() {
        Step step = getCurrentStep();
        int code = step.code();
        if (doneSteps == null) {
            JobMetadata metadata = engine.getStepRegistry().getMetadata(jobClass);
            doneSteps = metadata.getDoneSteps();
        }
        for(StepMetadata s: doneSteps) {
            if (code == s.getCode()) {
                return true;
            }
        }
        return false;
    }

    public WinderJobContext getJobContext() {
        return jobContext;
    }

    public final TaskState doExecute(Task<TI, TR> task) throws Exception {
        //Inject configuration
        PropertyUtil.inject(task, engine.getConfiguration());

        return executeTask(task);
    }

    protected TaskState executeTask(Task<TI, TR> task) throws Exception {
        return task.execute(this, taskInput, taskResult);
    }

    public boolean execute(Task<TI, TR> task) throws Exception {
        return doExecute(task) == TaskState.COMPLETED;
    }

    public void setJobStatus(StatusEnum jobStatus) {
        this.jobStatus = jobStatus;
    }
}
