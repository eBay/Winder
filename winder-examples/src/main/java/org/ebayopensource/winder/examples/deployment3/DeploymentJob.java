/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 * <p>
 * Licensed under the MIT license.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * <p>
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.examples.deployment3;

import org.ebayopensource.winder.*;
import org.ebayopensource.winder.anno.DoneStep;
import org.ebayopensource.winder.anno.ErrorStep;
import org.ebayopensource.winder.anno.FirstStep;
import org.ebayopensource.winder.anno.Job;
import org.ebayopensource.winder.examples.deployment3.tasks.*;


/**
 * Deployment job with multiple tasks and grouping the instances.
 * The job only has the work flow. All the business logic been moved to task classes.
 * It also leverages the existing TaskStatusData to hold the status of instance.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/28/16.
 * @version 1.0
 */
@Job(type = "Deployment3")
public enum DeploymentJob implements Step<TaskInput, TaskResult, TaskContext<TaskInput, TaskResult>> {

    @FirstStep
    PRE_CHECK(10) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            Precheck precheck = new Precheck();
            if (ctx.execute(precheck)) {
                ctx.setCurrentStep(DOWNLOAD);
            }
            else {
                ctx.setCurrentStep(ERROR);
            }
        }
    },

    DOWNLOAD(20) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            Download download = new Download();
            if (ctx.execute(download)) {
                ctx.setCurrentStep(VALIDATE);
            }
            else {
                ctx.setCurrentStep(ERROR);
            }
        }
    },

    VALIDATE(30) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            Validate validate = new Validate();
            TaskState state = ctx.doExecute(validate);
            Step nextStep = VALIDATE;
            switch (state) {
                case NEXT:
                    nextStep = STARTUP;
                    break;
                case NEXT_GROUP:
                    nextStep = DOWNLOAD;
                    break;
                case COMPLETED:
                    nextStep = DONE;
                    break;
                case WAITING:
                    nextStep = VALIDATE;
                    break;
                case ERROR:
                    nextStep = ERROR;
                    break;
            }
            ctx.setCurrentStep(nextStep);
        }
    },

    STARTUP(40) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            Startup startup = new Startup();
            if (ctx.execute(startup)) {
                ctx.setCurrentStep(VALIDATE);
            }
            else {
                ctx.setCurrentStep(ERROR);
            }
        }
    },
    @DoneStep
    DONE(200) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            Done done = new Done();
            ctx.execute(done);
            System.out.println("All done!");
        }
    },

    @ErrorStep
    ERROR(400) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("ERROR");
        }
    };

    private final int code;

    public int code() {
        return code;
    }

    DeploymentJob(final int code) {
        this.code = code;
    }
}
