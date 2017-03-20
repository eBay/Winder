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
package org.ebayopensource.winder;

import org.ebayopensource.winder.DetailLevel;
import org.ebayopensource.winder.StatusEnum;

import java.util.Date;

/**
 * Jobs List Filter
 *
 * @author Sheldon Shao xshao@ebay.com on 3/18/17.
 * @version 1.0
 */
public class JobListFilter {

    public String owner;
    public String jobName;
    public String excludeJobName;

    public Date startDate;
    public Date endDate;

    public DetailLevel level = DetailLevel.SUMMARY;

    public int skip;
    public int limit;

    public StatusEnum jobStatus = StatusEnum.UNKNOWN;


    public boolean isJobCleanUp = false;

    // If searchCronJobsOnly is set to true, will ignore other search
    // parameters
    public boolean searchCronJobsOnly = false;

    public String[] searchNames;
    public Object[] values;

    public boolean isSearchList() {
        return (owner == null && jobName == null && endDate == null && startDate == null
                && jobStatus == StatusEnum.UNKNOWN && !searchCronJobsOnly);
    }

    public boolean isSimpleList() {
        return (owner == null && jobName == null && excludeJobName == null && endDate == null
                && startDate == null && (searchNames == null && values == null)
                && jobStatus == StatusEnum.UNKNOWN && !searchCronJobsOnly);
    }

    public boolean isAllJobsSimpleList() {
        return (owner == null && jobName == null && excludeJobName == null && endDate == null
                && startDate == null
                && (searchNames == null && values == null) && (jobStatus == StatusEnum.EXECUTING
                || jobStatus == StatusEnum.PAUSED || jobStatus == StatusEnum.ERROR)
                && !searchCronJobsOnly);
    }

    public boolean isJobIdList() {
        return (owner == null && jobName == null && excludeJobName == null
                && (searchNames != null && searchNames.length == 1
                && ("id".equals(searchNames[0]) || "category".equals(searchNames[0])))
                && (values != null && values.length == 1) && jobStatus == StatusEnum.UNKNOWN
                && !searchCronJobsOnly);
    }

    public boolean isJobNameList() {
        return (owner == null && (jobName != null || excludeJobName != null) && endDate == null
                && startDate == null && (searchNames == null && values == null)
                && jobStatus == StatusEnum.UNKNOWN && !searchCronJobsOnly);
    }
}
