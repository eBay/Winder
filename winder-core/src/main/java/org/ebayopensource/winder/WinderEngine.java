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

import org.ebayopensource.winder.metadata.StepRegistry;

import java.util.Date;

/**
 * Winder Runtime Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderEngine {

    /**
     * Return cluster type
     *
     * @return The quartz cluser type
     */
    String getClusterName();

    /**
     * Return configuration
     *
     * @return Configuration
     */
    WinderConfiguration getConfiguration();

    /**
     * Winder Job Detail Factory
     *
     * @return Job Detail Factory
     */
    WinderJobDetailFactory getJobDetailFactory();

    /**
     * Return SchedulerManager
     *
     * @return SchedulerManager
     */
    WinderSchedulerManager getSchedulerManager();


    /**
     * Fast method
     *
     * @param jobClass Job class
     * @throws WinderScheduleException When data base exception
     */
    JobId scheduleJob(Class jobClass) throws WinderScheduleException;

    /**
     * Fast method
     *
     * @param taskInput TaskInput
     * @throws WinderScheduleException When data base exception
     */
    <TI extends TaskInput> JobId scheduleJob(TI taskInput) throws WinderScheduleException;

    /**
     * Job Detail Merger
     *
     * @return Job Detail Merger
     */
    WinderJobDetailMerger getJobDetailMerger();

    /**
     * Job Error Listener
     *
     * @return Job Error Listener
     */
    <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> WinderJobErrorListener<TI, TR, C> getJobErrorListener(C taskContext);

    /**
     * Step Registry
     *
     * @return Current StepRegistry implementation
     */
    StepRegistry getStepRegistry();


    void start();

    void stop();
}
