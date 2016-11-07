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
    }

    @Override
    public int getJobDuration() {
        return jobDuration;
    }

    public void setJobDuration(int jobDuration) {
        this.jobDuration = jobDuration;
    }

    @Override
    public Date getJobStartTime() {
        if (jobStartTime == null) {
            jobStartTime = new Date();
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
    }

    @Override
    public Class getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class jobClass) {
        this.jobClass = jobClass;
    }
}
