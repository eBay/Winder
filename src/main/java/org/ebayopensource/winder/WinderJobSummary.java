package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;

import java.util.List;

/**
 * Job summary, it only has some basic information
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJobSummary<TI extends TaskInput, TR extends TaskResult> {

    /**
     * Return parent jobid
     *
     * @return
     */
    JobId getParentJobId();


    JobId[] getChildJobIds();

    /**
     * Return all user actions
     *
     * @return all user actions, if there is no UserAction, it returns Collections.EMPTY_LIST
     */
    List<UserAction> getUserActions();

    /**
     * Add new user action
     */
    UserAction addUserAction(UserActionType type, String message, String owner);

    /**
     * Return all status updates
     * @return status updates, if there is no UserAction, it returns Collections.EMPTY_LIST
     */
    List<StatusUpdate> getUpdates();

    /**
     * Add a new status update
     *
     * @param status StatusEnum
     * @param message Message
     */
    StatusUpdate addUpdate(StatusEnum status, String message);


    StatusUpdate addUpdate(StatusEnum status, String message, Throwable ex);

//    public StatusUpdateData changeJobData(final JobStatusEnum status,
//                                          final PaasDetails details) {
//
//        setStatus(status);
//        StringBuilder buf = new StringBuilder();
//        if (details.hasErrors()) {
//            buf.append(StringUtils.join(details.getErrors(), " "));
//            if (details.getExceptionStack() != null) {
//                buf.append('\n');
//                buf.append(ServletUtils.formatString(details.getExceptionStack(), MAX_STACK, true));
//            }
//        } else if (details.hasMessages()) {
//            buf.append(StringUtils.join(details.getMessages(), " "));
//        }
//        setStatusMsg(buf.toString());
//        return createStatusUpdateData();
//    }
//
//    public StatusUpdateData changeJobData(final JobStatusEnum status,
//                                          final Throwable ex, final String msg) {
//        final StatusUpdateData lastStatusUpdate = StatusUpdateDataUtil.getLastStatusUpdateDate(JobUtils.JOBSTATUSUPDATE_PREFIX, m_map);
//        if (lastStatusUpdate == null ||
//                !lastStatusUpdate.getExecutionStatus().equals(status) ||
//                !lastStatusUpdate.getStatusMessage().equals(msg)) {
//
//            setStatus(status);
//            if ((ex != null) && !(ex instanceof IllegalArgumentException)) {
//                StringBuilder buf = new StringBuilder();
//                buf.append(msg);
//                StringWriter sw = new StringWriter();
//                PrintWriter pw = new PrintWriter(sw);
//
//                StringBuffer tmp = sw.getBuffer();
//                tmp.append('\n');
//                // start stack trace on its own line
//                ex.printStackTrace(pw); // NOSONAR
//                buf.append(" **** ");
//                buf.append(ServletUtils.formatString(tmp.toString(), MAX_STACK, true));
//                setStatusMsg(buf.toString());
//            } else {
//                setStatusMsg(msg);
//            }
//            return createStatusUpdateData();
//        }
//        return lastStatusUpdate;
//    }

    String getTarget();

    void setTarget(String target);

    String getAction();


    void setAction(String action);

    /**
     * Return result
     *
     * @return Job status result
     */
    TR getTaskResult();


    void setTaskResult(TR result);

    /**
     * Task input
     *
     * @return
     */
    TI getTaskInput();


    void setTaskInput(TI taskInput);


    String getOwner();


    TaskStatusData addTaskStatus(String taskId, String taskName);


    TaskStatusData getTaskStatus(String taskId);

    /**
     * Return all task status
     * @return
     */
    List<TaskStatusData> getAllTaskStatuses();

//
//    public int getPercentComplete() {
//        String pctCmpltString = m_map.getString("KEY_JOBPCTCOMPLETE"); //for eoe upgrade, NOT SURE if this KEY_JOBPCTCOMPLETE will work
//        int result = 0;
//        if (pctCmpltString != null) {
//            try {
//                result = Integer.parseInt(pctCmpltString);
//            } catch (NumberFormatException e) {
//                throw new IllegalArgumentException("bad pct complete " + pctCmpltString, e);
//            }
//        }
//        if (result < 0 || result > 100) {
//            throw new IllegalArgumentException("bad pct complete " + pctCmpltString);
//        }
//        return result;
//    }
}
