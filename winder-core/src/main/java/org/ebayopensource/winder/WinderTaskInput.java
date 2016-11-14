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

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.ebayopensource.winder.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Winder Task Input
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public class WinderTaskInput extends ParametersMap<Object> implements TaskInput {

    private Class jobClass;
    private String jobType;
    private String jobGroup;
    private String jobCategory ="default";
    private String jobOwner = "unknownUser";
    private int stepInterval = 10;
    private int jobDuration = 24 * 60 * 60;
    private Date jobStartTime;

    public WinderTaskInput() {
    }

    public WinderTaskInput(Class jobClass) {
        setJobClass(jobClass);
    }

    public WinderTaskInput(Parameters<Object> map) {
        super(map);
    }

    @Override
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Override
    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    @Override
    public String getJobOwner() {
        return jobOwner;
    }

    @Override
    public void setJobOwner(String jobOwner) {
        this.jobOwner = jobOwner;
    }

    @Override
    public int getStepInterval() {
        return stepInterval;
    }

    public void setStepInterval(int stepInterval) {
        this.stepInterval = stepInterval;
        put("winder_step_interval", stepInterval);
    }

    @Override
    public int getJobDuration() {
        return jobDuration;
    }

    public void setJobDuration(int jobDuration) {
        this.jobDuration = jobDuration;
        put("winder_job_duration", jobDuration);
    }

    @Override
    public Date getJobStartTime() {
        if (jobStartTime == null) {
            long startTime = getLong("winder_job_start_time", -1);
            if (startTime > 0) {
                jobStartTime = new Date(startTime);
            }
            else {
                jobStartTime = new Date();
            }
        }
        return jobStartTime;
    }

    private static Logger log = LoggerFactory.getLogger(WinderTaskInput.class);

    @Override
    public String toJson() {
        try {
            return JsonUtil.writeValueAsString(this);
        } catch (IOException e) {
            log.warn("Convert to json exception", e);
            throw new IllegalStateException("Illegal state");
        }
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
        put("winder_job_start_time", jobStartTime.getTime());
    }

    @Override
    public Class getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
}
