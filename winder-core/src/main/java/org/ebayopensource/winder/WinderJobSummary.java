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
     * Return all owner actions
     *
     * @return all owner actions, if there is no UserAction, it returns Collections.EMPTY_LIST
     */
    List<UserAction> getUserActions();

    /**
     * Add new owner action
     */
    UserAction addUserAction(UserActionType type, String message, String owner);


    void addUserAction(UserAction userAction);

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


//    TaskStatusData getTaskStatus(String taskId);

    /**
     * Return all task status
     * @return
     */
    List<TaskStatusData> getAllTaskStatuses();
}
