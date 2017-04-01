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
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
@PersistJobDataAfterExecution
public class QuartzJob implements Job {

    private static Logger log = LoggerFactory.getLogger(QuartzJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Get job data for job... this is the stateful one, we're ignoring data in trigger
        JobDetail quartzJobDetail = context.getJobDetail();
        JobDataMap runtimeMap = quartzJobDetail.getJobDataMap();

        WinderEngine engine = QuartzEngine.getInstance();
        QuartzJobContext ctx = new QuartzJobContext(engine, context);
        WinderJobDetail runtimeDetail = ctx.getJobDetail();
        String className = runtimeMap.getString(KEY_JOB_CLASS);

        WinderJob job = null;
        Runnable runnable = null;
        try {
            if (className == null) {
                throw new IllegalArgumentException("Missing job class name");
            }

            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            catch(Exception ex) {
                clazz = Class.forName(className);
            }

            if (Step.class.isAssignableFrom(clazz)) { //Multiple steps
                job = new WinderStair(engine, clazz);
            }
            else if (Runnable.class.isAssignableFrom(clazz)) {
                runnable = (Runnable)clazz.newInstance();
                if (runnable instanceof WinderEngineAware) {
                    ((WinderEngineAware)runnable).setWinderEngine(engine);
                }
            }
            else {
                throw new IllegalStateException("Unsupported job class:" + className);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Creating job exception", e);
            }
            JobExecutionException ex = new JobExecutionException("unable to create job object",e);
            ex.setUnscheduleAllTriggers(true); // don't try this one again
            ctx.setError();
            ctx.setStatusMessage("Creating job exception", e);
            throw ex;
        }

        if (!runtimeMap.containsKey(KEY_JOB_START_DATE)) {
            runtimeMap.put(KEY_JOB_START_DATE, System.currentTimeMillis());
        }

        if ( StatusEnum.CANCEL_IN_PROGRESS != ctx.getJobStatus() &&
                StatusEnum.EXECUTING != ctx.getJobStatus() ) {
            ctx.setJobStatus(StatusEnum.EXECUTING);
        }

        // Execute the job
        try {
            if (job != null) {
                job.execute(ctx);
            }
            else if (runnable != null) {
                runnable.run();
            }
        } catch (WinderException je) {
            JobExecutionException ex = new JobExecutionException("exception running job",je);
            ex.setUnscheduleAllTriggers(true); // don't try this one again
            ctx.setError();
            ctx.setStatusMessage("Executing job exception", je);
            throw ex;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Executing job exception", e);
            }
            JobExecutionException ex = new JobExecutionException("Unexpected exeception running job", e);
            ex.setUnscheduleAllTriggers(true); // don't try this one again
            ctx.setError();
            ctx.setStatusMessage("Executing job unknown exception", e);
            throw ex;
        }

        if (job != null) {
            runtimeDetail.sync();

            StatusEnum executionResultStatus = ctx.getJobStatus();

            if (!executionResultStatus.isDone()) { // Merge the status from DB and
                WinderJobDetailMerger merger = engine.getJobDetailMerger();

                try {
                    WinderJobDetail dbDetail = engine.getSchedulerManager().getJobDetail(runtimeDetail.getJobId());
                    merger.merge(dbDetail, runtimeDetail);
                } catch (Exception e) {
                    // KEEPME
                    // fail to look up, should not happen, leave the status along
                    log.error("Finishing job exception", e);
                }
            }
        }
    }
}
