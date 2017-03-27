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

import org.ebayopensource.common.util.Parameters;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Winder Job Detail, it has all the information of the job
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJobDetail<TI extends TaskInput, TR extends TaskResult> extends Comparable<WinderJobDetail> {

    /**
     * Return current jobId
     *
     * @return
     */
    JobId getJobId();

    /**
     * Return parent jobid
     *
     * @return
     */
    JobId getParentJobId();

    /**
     * return child jobIds if there is any.
     *
     * @return null when there is no child job id.
     */
    JobId[] getChildJobIds();

    /**
     * Add new child job id
     *
     * @param jobId
     */
    void addChildJobIds(JobId jobId);

    /**
     * JobIds
     *
     * @param jobIds
     */
    void setChildJobIds(JobId[] jobIds);

    String getDescription();

    boolean isDurable();

    boolean isPersistJobDataAfterExecution();

    boolean isConcurrentExectionDisallowed();

    boolean requestsRecovery();

    /**
     * Auto Pause flag is to make the job pause when it finished part of job
     *
     * @return is AutoPause flag set
     */
    boolean isAutoPause();

    /**
     * Set Auto Pause flag
     *
     * @param autoPause Auto Pause flag
     */
    void setAutoPause(boolean autoPause);

    /**
     * Job Created Date
     *
     * @return Job Created Date
     */
    Date getCreated();

    Date getStartTime();

    Date getEndTime();

    void setStartTime(Date date);

    void setEndTime(Date date);

    StatusEnum getStatus();

    void setStatus(StatusEnum status);


    TI getInput();


    TR getResult();

    /**
     * Return jobData as Parameters
     *
     * @return jobData as Parameters
     */
    Parameters<Object> getData();

    /**
     * Return all owner actions
     *
     * @return all owner actions, if there is no UserAction, it returns Collections.EMPTY_LIST
     */
    List<UserAction> getUserActions();

    /**
     * Add new owner action
     *
     * @param type UserActionType
     * @param message Messgae
     * @param owner Owner
     */
    UserAction addUserAction(UserActionType type, String message, String owner);

    /**
     * For merging
     *
     * @param userAction User Action
     * @return
     */
    void addUserAction(UserAction userAction);

    /**
     * Is the job waiting for User Action
     *
     * @return waiting for User Action
     */
    boolean isAwaitingForAction();

    /**
     * Set awaiting for action flag
     *
     * @param awaitingForAction awaiting for action flag
     */
    void setAwaitingForAction(boolean awaitingForAction);

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

    /**
     * Convert to Map so that it can be converted to JSON easily
     *
     * @return Convert to Map so that it can be converted to JSON easily
     */
    Map<String, Object> toMap();

    /**
     * To Json
     *
     * @return Convert the input parameters to Json
     */
    String toJson();
}
