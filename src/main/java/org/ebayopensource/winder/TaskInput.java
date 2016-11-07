package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;

import java.util.Date;

/**
 * Job Input
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public interface TaskInput extends Parameters<Object> {

    /**
     * Job Class
     *
     * @return Job Class
     */
    Class getJobClass();

    /**
     * Job Type
     *
     * @return Job Type
     */
    String getJobType();

    /**
     * A category for querying the jobs
     *
     * @return Category
     */
    String getJobCategory();

    /**
     * Job owner, who created this job?
     *
     * @return Job owner
     */
    String getJobOwner();


    void setJobOwner(String jobOwner);

    /**
     * Step interval in seconds
     *
     * @return Interval
     */
    int getStepInterval();

    /**
     * Job duration in seconds
     *
     * @return Job duration
     */
    int getJobDuration();

    /**
     * If it is NULL, means start it immediately
     *
     * @return
     */
    Date getJobStartTime();


    /**
     * To Json
     *
     * @return
     */
    String toJson();
}
