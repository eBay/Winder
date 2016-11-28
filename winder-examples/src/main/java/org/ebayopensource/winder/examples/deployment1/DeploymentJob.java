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
package org.ebayopensource.winder.examples.deployment1;

import org.ebayopensource.deployment.DeploymentAPI;
import org.ebayopensource.deployment.InstanceState;
import org.ebayopensource.deployment.SimpleDeploymentAPI;
import org.ebayopensource.winder.*;
import org.ebayopensource.winder.anno.DoneStep;
import org.ebayopensource.winder.anno.ErrorStep;
import org.ebayopensource.winder.anno.FirstStep;
import org.ebayopensource.winder.anno.Job;

import java.util.List;

/**
 * This simple deployment sample just shows the multiple work flow.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
@Job(type = "Deployment1")
public enum DeploymentJob implements Step<TaskInput, TaskResult, TaskContext<TaskInput, TaskResult>> {

    @FirstStep
    PRE_CHECK(10) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            TaskInput input = ctx.getTaskInput();
            TaskResult result = ctx.getTaskResult();

            List<String> fqdns = input.getList("targets");
            List<InstanceState> instances = deploymentAPI.preCheck(fqdns);
            result.put("instances", instances);
            ctx.setCurrentStep(DOWNLOAD);
        }
    },

    DOWNLOAD(20) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            TaskInput input = ctx.getTaskInput();
            TaskResult result = ctx.getTaskResult();

            String software = input.getString("software");
            if (software == null) { //Go to error
                throw new IllegalArgumentException("No software specified");
            }

            List<InstanceState> instances = (List<InstanceState>)result.get("instances");
            deploymentAPI.download(instances, software);
            result.put("last_step", ctx.getCurrentStep().name());
            ctx.setCurrentStep(VALIDATE);
        }
    },

    VALIDATE(30) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            TaskResult result = ctx.getTaskResult();

            List<InstanceState> instances = (List<InstanceState>)result.get("instances");

            String lastStep = result.getString("last_step");
            List<Integer> progress = deploymentAPI.validate(instances, lastStep);
            boolean allDone = true;
            for(Integer p : progress) {
                if (p != 100) {
                    allDone = false;
                }
            }

            if (allDone) { //Go to next step
                if ("DOWNLOAD".equals(lastStep)) {
                    ctx.setCurrentStep(STARTUP);
                }
                else if ("STARTUP".equals(lastStep)) {
                    ctx.setCurrentStep(DONE);
                }
            }
        }
    },

    STARTUP(40) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            TaskInput input = ctx.getTaskInput();
            TaskResult result = ctx.getTaskResult();

            String software = input.getString("software");
            List<InstanceState> instances = (List<InstanceState>)result.get("instances");
            deploymentAPI.startup(instances, software);
            result.put("last_step", ctx.getCurrentStep().name());
            ctx.setCurrentStep(VALIDATE);
        }
    },
    @DoneStep
    DONE(200) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> ctx) throws Exception {
            TaskResult result = ctx.getTaskResult();
            List<InstanceState> instances = (List<InstanceState>)result.get("instances");
            for(InstanceState instance: instances) {
                if (instance.getCode() == StatusEnum.EXECUTING) {
                    instance.setCode(StatusEnum.COMPLETED);
                }
            }
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

    //It can be injected from some where
    private static DeploymentAPI deploymentAPI = new SimpleDeploymentAPI();
}
