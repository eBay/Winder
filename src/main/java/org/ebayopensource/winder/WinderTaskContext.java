package org.ebayopensource.winder;

import org.ebayopensource.common.config.PropertyUtil;
import org.ebayopensource.common.util.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public WinderTaskContext(WinderJobContext jobContext, Class<? extends Step> jobClass) throws WinderScheduleException {
        this.jobClass = jobClass;
        this.jobContext = jobContext;
        this.engine = jobContext.getEngine();

        init();
    }

    protected void init() throws WinderScheduleException {
        WinderSchedulerManager schedulerManager = engine.getSchedulerManager();
        jobDetail = schedulerManager.getJobDetail(jobContext.getJobId());

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

    public final void setCurrentStep(Step step) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Job {%s} status is: {%s} nextStep is {%s}", getJobId(),
                    getJobStatus(), step));
        }
        jobContext.setJobStep(step.code());
    }

    @Override
    public Step<TI, TR, ? extends WinderTaskContext> getCurrentStep() {
        Step step = engine.getStepRegistry().lookup(jobClass, jobContext.getJobStep());
        if (step == null) {
            step = engine.getStepRegistry().getFirstStep(jobClass);
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Job {%s} execution status {%s} step {%s}", getJobId(), getJobStatus(), step));
        }
        return step;
    }

    private Step[] doneSteps = null;

    @Override
    public boolean isDone() {
        Step step = getCurrentStep();
        int code = step.code();
        if (doneSteps == null) {
            doneSteps = engine.getStepRegistry().getDoneSteps(jobClass);
        }
        for(Step s: doneSteps) {
            if (code == s.code()) {
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
