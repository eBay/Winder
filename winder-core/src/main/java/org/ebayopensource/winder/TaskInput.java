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
     * Job group, it is same as the group in JobId,
     * If no group specified, winder uses formatted date as group.
     *
     * @return Job group, it is same as the group in JobId
     */
    String getJobGroup();

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
