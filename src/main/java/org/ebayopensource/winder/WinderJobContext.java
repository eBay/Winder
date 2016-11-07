package org.ebayopensource.winder;

/**
 * I Winder Job Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJobContext {

    /**
     * Winder Engine
     *
     * @return
     */
    WinderEngine getEngine();

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

    int getJobStep();

    void setJobStep(int step);

    boolean isRecovering();

    String getStatusMessage();

    void setStatusMessage(String msg);

    void setStatusMessage(String msg, Throwable t);

    boolean isAwaitingForAction(boolean isAwaiting);

    void setAwaitingForAction(boolean isAwaiting);

    void setComplete();

    void setError();

    void setCompleteWithWarning();

    StatusEnum getJobStatus();

    void setJobStatus(StatusEnum status);

    WinderJobSummary getJobStateData();

    JobId[] getChildJobs();

    WinderJobDetail getJobDetail();

    void updateJobData();
}
