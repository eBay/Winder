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
package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.*;
import org.ebayopensource.winder.metadata.StepRegistry;
import org.ebayopensource.winder.metadata.WinderStepRegistry;

/**
 * Quartz Implementation
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class QuartzEngine implements WinderEngine {

    private String clusterName = "winder";
    private WinderStepRegistry stepRegistry = new WinderStepRegistry();

    private WinderConfiguration configuration;

    private QuartzSchedulerManager schedulerManager;

    private boolean started = false;

    private WinderJobDetailFactory jobDetailFactory;

    private QuartzJobDetailMerger jobDetailMerger = new QuartzJobDetailMerger();

    private static WinderEngine instance;

    public static WinderEngine getInstance() {
        return instance;
    }

    protected static void setInstance(WinderEngine engine) {
        instance = engine;
    }

    public QuartzEngine() {
        this(new QuartzConfiguration());
    }


    //For extension
    public QuartzEngine(WinderConfiguration configuration) {
        this.configuration = configuration;
        setClusterName(configuration.getString("winder.cluster", "winder"));

        this.jobDetailFactory = new QuartzJobDetailFactory(this);
        this.schedulerManager = new QuartzSchedulerManager(this);
        instance = this;

    }

    @Override
    public String getClusterName() {
        return clusterName;
    }


    @Override
    public WinderConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public WinderJobDetailFactory getJobDetailFactory() {
        return jobDetailFactory;
    }

    @Override
    public WinderSchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    @Override
    public JobId scheduleJob(Class jobClass) throws WinderScheduleException {
        return scheduleJob(new WinderTaskInput(jobClass));
    }

    @Override
    public <TI extends TaskInput> JobId scheduleJob(TI taskInput) throws WinderScheduleException {
        return getSchedulerManager().scheduleJob(taskInput);
    }

    @Override
    public WinderJobDetailMerger getJobDetailMerger() {
        return jobDetailMerger;
    }

    @Override
    public <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> WinderJobErrorListener<TI, TR, C> getJobErrorListener(C taskContext) {
        return null;
    }

    @Override
    public StepRegistry getStepRegistry() {
        return stepRegistry;
    }

    @Override
    public synchronized void start() {
        if (schedulerManager != null && !started) {
            schedulerManager.start();
            started = true;
        }
    }

    @Override
    public synchronized void stop() {
        if (schedulerManager != null && started) {
            schedulerManager.stop();
        }
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setConfiguration(WinderConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setJobDetailMerger(QuartzJobDetailMerger jobDetailMerger) {
        this.jobDetailMerger = jobDetailMerger;
    }
}
