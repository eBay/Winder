package org.ebayopensource.winder;

/**
 * Job Status Enum
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public enum StatusEnum {
    SUBMITTED,
    EXECUTING,
    CANCEL_IN_PROGRESS, //to a job this status is equivalent to EXECUTING, except to mark that a cancel request is in progress
    PAUSED,
    COMPLETED(true),
    ERROR(true),
    CANCELLED(true),
    UNKNOWN,
    WARNING(true);

    private boolean done = false;

    StatusEnum() {
        this(false);
    }

    StatusEnum(boolean done) {
        this.done = done;
    }

    /**
     * Means the jobs is done
     *
     * @return Done
     */
    public boolean isDone() {
        return done;
    }
}
