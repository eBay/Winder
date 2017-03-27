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
import org.ebayopensource.deployment.GroupStrategy;
import org.ebayopensource.deployment.InstanceState;
import org.ebayopensource.winder.*;

import java.util.List;

/**
 * Validate
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
public class Validate implements Task<TaskInput, TaskResult> {

    /**
     * It should be inject from
     */
    @InjectProperty(name="deployment_api")
    private DeploymentAPI deploymentAPI;

    @InjectProperty(name="group_strategy")
    private GroupStrategy groupStrategy;

    @Override
    public TaskState execute(TaskContext<TaskInput, TaskResult> ctx, TaskInput input, TaskResult result) throws Exception {
        String lastStep = result.getString("last_step");

        WinderJobSummary<TaskInput, TaskResult> summary = ctx.getJobContext().getJobSummary();
        List<TaskData> taskStatuses = summary.getAllTasks();
        int groupId = ctx.getGroupId();
        int maxGroup = 3; //From some where
        List<InstanceState> groupInstances = groupStrategy.getGroup(taskStatuses, groupId, maxGroup);

        String step = ctx.getCurrentStep().name();
        //Set action and update information
        for(InstanceState instance: groupInstances) {
            TaskData statusData = instance.getTaskData();
            statusData.setAction(step);
            statusData.addUpdate(StatusEnum.EXECUTING, "Validating " + lastStep);
        }
        ctx.getJobContext().addUpdate(StatusEnum.EXECUTING, "Validating " + lastStep + " group:" + groupId);


        List<Integer> progress = deploymentAPI.validate(groupInstances, lastStep);
        boolean allDone = true;
        for(Integer p : progress) {
            if (p != 100) {
                allDone = false;
            }
        }

        if (allDone) { //Go to next step
            if ("DOWNLOAD".equals(lastStep)) {
                return TaskState.NEXT;
            }
            else if ("STARTUP".equals(lastStep)) {
                //Set completed status
                for(InstanceState instance: groupInstances) {
                    TaskData statusData = instance.getTaskData();
                    statusData.setExecutionStatus(StatusEnum.COMPLETED);
                }
                if (groupId < maxGroup) { //Go back to download
                    ctx.setGroupId(++groupId);
                    return TaskState.NEXT_GROUP;
                }
                else {
                    return TaskState.COMPLETED;
                }
            }
        }

        System.out.println("JobDetail:==========================\r\n" + ctx.getJobContext().getJobDetail().toJson());
        return TaskState.WAITING;
    }
}
