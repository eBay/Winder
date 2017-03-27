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
package org.ebayopensource.winder.examples.deployment2.tasks;

import org.ebayopensource.common.config.InjectProperty;
import org.ebayopensource.deployment.DeploymentAPI;
import org.ebayopensource.deployment.InstanceState;
import org.ebayopensource.winder.*;

import java.util.List;

/**
 * Pre check
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
public class Precheck implements Task<TaskInput, TaskResult> {
    /**
     * Deployment API
     */
    @InjectProperty(name="deployment_api")
    private DeploymentAPI deploymentAPI;

    @Override
    public TaskState execute(TaskContext<TaskInput, TaskResult> ctx, TaskInput input, TaskResult result) throws Exception {
        List<String> targetServers = input.getStringList("targets");

        List<InstanceState> states = deploymentAPI.preCheck(targetServers);
        //Show status update in job summary
        WinderJobSummary summary = ctx.getJobContext().getJobSummary();
        summary.addUpdate(StatusEnum.EXECUTING, "Pre checked, total instances:" + targetServers.size());

        //Set status for each task
        for(int i = 0; i < targetServers.size(); i ++) {
            InstanceState state = states.get(i);
            TaskData taskData = summary.addTask(targetServers.get(i), state.getCode().name());
            taskData.setTarget(state.getFqdn());
            taskData.setAction(ctx.getCurrentStep().name());
            taskData.setExecutionStatus(StatusEnum.EXECUTING);
        }

        System.out.println("JobDetail:==========================\r\n" + ctx.getJobContext().getJobDetail().toJson());
        return TaskState.COMPLETED;
    }
}
