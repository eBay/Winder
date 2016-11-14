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

import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.winder.*;
import org.ebayopensource.winder.metadata.JobMetadata;
import org.ebayopensource.winder.util.Guid;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import java.util.Date;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.KEY_JOBOWNER;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.KEY_JOBSTAGE;

/**
 * Job Detail Factory
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public class QuartzJobDetailFactory implements WinderJobDetailFactory {

    private WinderEngine engine;

    public QuartzJobDetailFactory(WinderEngine engine) {
        this.engine = engine;
    }

    private Guid guid = new Guid();

    @Override
    public WinderJobDetail createJobDetail(TaskInput input) {
        Class clazz = input.getJobClass();

        String jobType = input.getJobType();
        jobType = jobType == null ? clazz.getSimpleName() : jobType;

        String jobOwner = input.getJobOwner();

        Date createDate = new Date();
        String jobName = jobType + "." + guid.nextPaddedGUID();

        String firstStep = "-1";
        JobMetadata metadata = null;
        if (Step.class.isAssignableFrom(clazz)) {
            //Make sure it was registered
            metadata = engine.getStepRegistry().register(clazz);
            firstStep = "-1";
        }
        else if (Runnable.class.isAssignableFrom(clazz)) {
            firstStep = "-1";
        }
        else {
            throw new IllegalStateException("Unsupported job type:" + clazz.getName());
        }

        /*
         * Group name priority:
         *
         * taskInput.group > @Job.group > winder default group name("formatted date")
         */
        String groupName = input.getJobGroup();
        if (StringUtils.isBlank(groupName)) {
            if (metadata != null) {
                groupName = metadata.getJobGroup();
                if (StringUtils.isBlank(groupName)) {
                    groupName = engine.formatShortDate(createDate);
                }
            }
        }

        JobDataMap map = new JobDataMap();
        String jobInput = input.toJson();

        String createDateStr = engine.formatDate(createDate);

        map.put(KEY_JOBCREATEDATE, createDateStr);
        map.put(KEY_JOBINPUT, jobInput);
        map.put(KEY_JOBSTATUS, StatusEnum.SUBMITTED.toString());
        map.put(KEY_JOBCLASS, clazz.getName());
        map.put(KEY_JOBSTAGE, firstStep);
        map.put(KEY_JOBOWNER, jobOwner);

        // Create Job Details
        JobDetail jd = JobBuilder.newJob(QuartzJob.class).withIdentity(jobName, groupName).storeDurably(true) // durability:
                // keep
                // even
                // when
                // no
                // triggers
                // left
                .requestRecovery(true) // recover: recover in cluster after
                // failures
                .usingJobData(map).build();
        JobId jobId = new QuartzJobId(groupName, jobName, engine.getClusterName());
        return new QuartzJobDetail(engine, jobId, jd);
    }
}
