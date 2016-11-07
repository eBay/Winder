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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.ebayopensource.winder.StatusEnum.*;
import static org.ebayopensource.winder.StatusEnum.PAUSED;

/**
 * Stair == Multiple Steps
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class WinderStair implements WinderJob {

    protected WinderEngine engine;

    protected WinderSchedulerManager schedulerManager;

    private boolean straightforward = true;

    private int maxSteps = 60;

    private Logger log;

    private Class<? extends Step> jobClass;

    private String jobType;

    public WinderStair(WinderEngine engine, Class<? extends Step> jobClass) {
        this.engine = engine;
        this.log = LoggerFactory.getLogger(jobClass);
        this.schedulerManager = engine.getSchedulerManager();
        this.jobClass = jobClass;
        this.jobType = engine.getStepRegistry().getJobType(jobClass);
    }

    protected TaskContext createContext(WinderJobContext ctx) throws Exception {
        return new WinderTaskContext<>(ctx, jobClass);
    }

    @Override
    public void execute(WinderJobContext ctx) throws WinderJobException {
        TaskContext taskContext;
        try {
            taskContext = createContext(ctx);
        } catch (final Exception e) {
            throw new WinderJobException("Failure initializing context", e);
        }

        if (straightforward) {
            int stepCount = 0;
            while (stepCount < maxSteps) {
                //Load the context from DB again, because other instance may cancel the job.
                Step currentStep = taskContext.getCurrentStep();
                if (doExecute(currentStep, taskContext, ctx)) {
                    if (currentStep.code()  == taskContext.getCurrentStep().code()) {
                        break;
                    }
                } else {
                    break;
                }

                if (taskContext.isForceBreak()) {
                    break;
                }

                StatusEnum statusEnum = taskContext.getJobStatus();
                if (statusEnum == PAUSED || statusEnum == CANCEL_IN_PROGRESS
                        || statusEnum == ERROR || statusEnum == CANCELLED
                        || statusEnum == COMPLETED) {
                    break;
                }

                //Fix for canceling
                WinderJobDetail dbDetail;
                try {
                    dbDetail = schedulerManager.getJobDetail(ctx.getJobId());
                    StatusEnum dbStatus = dbDetail.getStatus();
                    if (dbStatus == CANCEL_IN_PROGRESS || dbStatus == StatusEnum.CANCELLED) {
                        break;
                    }

                    /*If the alert log has been updated in the job details,
                    then refresh the jobDetails in memory from the DB*/
                    if (dbDetail.isAwaitingForAction()){
                        break;
                    }
                } catch (Exception e) {
                    log.error(jobType + " check the status of job from db error", e);
                }

                stepCount ++;

                ctx.updateJobData();
            }
        }
        else {
            Step currentStep = taskContext.getCurrentStep();
            doExecute(currentStep, taskContext, ctx);
        }
    }


    protected boolean doExecute(final Step currentStep, TaskContext stepContext, final WinderJobContext ctx) throws WinderJobException {
        try {
            /**
             * Determine if the job has been running for too long. We prevent too many job step updates to prevent looping that
             * might fill the database and indicate the job is not completing as desired.
             */
            WinderJobSummary summary = ctx.getJobStateData();
            List<StatusUpdate> updates = summary.getUpdates();
            if (updates != null && updates.size() >= maxSteps) {
                throw new WinderJobException("Winder stair max stages (" + maxSteps + ") exceeded. Runaway job terminated.");
            }
            currentStep.process(stepContext);
            return true;
        } catch (final Exception e) {
            log.error(jobType + " unexpected exception occurred", e);

            WinderJobErrorListener listener = engine.getJobErrorListener(stepContext);
            if (listener != null) {
                listener.onError(currentStep, stepContext);
            }
            moveToError(stepContext, ctx, currentStep);
            return false;
        }
    }

    protected void moveToError(TaskContext taskContext, WinderJobContext ctx, Step currentStep) {
        Step errorStep = getErrorStep();
        if (errorStep != null && currentStep.code() != errorStep.code()) {
            taskContext.setCurrentStep(errorStep);
        } else {
            ctx.setError();
        }
    }

    private Step errorStep;

    protected Step getErrorStep() {
        if (errorStep == null) {
            errorStep = engine.getStepRegistry().getErrorStep(jobClass);
        }
        return errorStep;
    }

    public WinderEngine getEngine() {
        return engine;
    }

    public boolean isStraightforward() {
        return straightforward;
    }

    public void setStraightforward(boolean straightforward) {
        this.straightforward = straightforward;
    }
}
