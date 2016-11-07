package org.ebayopensource.winder;


/**
 * Task State
 *
 * Created by xshao on 9/16/16.
 */
public enum TaskState {
    /**
     * Executing or waiting
     *
     * That means it is still running in current step
     */
    WAITING,

    /**
     * To run next step
     */
    NEXT,

    /**
     * To run next group (go to the first step of one group)
     */
    NEXT_GROUP,

    /**
     * To skip the following step and jump to next next step
     */
    SKIP,

    /**
     * The step is completed
     */
    COMPLETED,

    /**
     * The job is on cancel pending
     */
    CANCEL_PENDING,

    /**
     * The job was cancelled
     */
    CANCELLED,

    /**
     * Current step is timed out
     */
    TIMEOUT,

    /**
     * Got error from current step
     */
    ERROR
}
