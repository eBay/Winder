package org.ebayopensource.winder;

/**
 * Task Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface TaskContext<TI extends TaskInput, TR extends TaskResult> {

//    <C extends TaskContext> Step<C> getCurrentStep(Class<? extends Step> clazz, Step firstStep);

    void setCurrentStep(Step<TI, TR, ? extends TaskContext> step);

    Step<TI, TR, ? extends TaskContext> getCurrentStep();

    boolean isDone();

    WinderJobContext getJobContext();

    TaskState doExecute(Task<TI, TR> task) throws Exception;

    boolean execute(Task<TI, TR> task) throws Exception;

    /**
     * Force break to wait next trigger
     *
     * @return break to wait next trigger
     */
    boolean isForceBreak();


    void setForceBreak(boolean forceBreak);


    StatusEnum getJobStatus();


    void setJobStatus(StatusEnum jobStatus);

    /**
     * Start from 1, if there is only one group, it returns 1
     *
     * @return groupId
     */
    int getGroupId();

    /**
     * Set groupId
     *
     * @param groupId GroupId
     */
    void setGroupId(int groupId);

    /**
     * Return JobId
     *
     * @return
     */
    JobId getJobId();

    /**
     * Return JobId in string.
     * @return
     */
    String getJobIdAsString();

    TI getTaskInput();

    TR getTaskResult();

//    void setTaskResult(Parameters<Object> result);
}
