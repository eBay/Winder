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

import org.ebayopensource.winder.metadata.JobMetadata;
import org.ebayopensource.winder.metadata.StepMetadata;

/**
 * Task Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface TaskContext<TI extends TaskInput, TR extends TaskResult> {

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
     * @return JobId
     */
    JobId getJobId();

    /**
     * Return JobId in string.
     *
     * @return JobId as String
     */
    String getJobIdAsString();

    TI getTaskInput();

    TR getTaskResult();

    /**
     * Return current step metadata
     *
     * @return current step metadata
     */
    StepMetadata getStepMetadata();

    /**
     * Job Metadata
     *
     * @return Job Metadata
     */
    JobMetadata getJobMetadata();
}
