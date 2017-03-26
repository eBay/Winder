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

import java.util.Date;

/**
 * Job Filter
 *
 * @author Sheldon Shao xshao@ebay.com on 3/25/17.
 * @version 1.0
 */
public interface JobFilter {

    /**
     * Is to use like ?
     *
     * @return default is false
     */
    boolean isLike();

    /**
     * Search Key
     *
     * @return Search Key (JOB_NAME or JOB_GROUP)
     */
    JobKeyField getKeyField();

    /**
     * Search value
     *
     * @return jobName or jobGroup
     */
    String getValue();

    /**
     * Start date (JobDetails.getCreated()
     *
     * @return Start Date
     */
    Date getStart();

    /**
     * End date (JobDetails.getCreated()
     *
     * @return End Date
     */
    Date getEnd();

    /**
     * Offset of the set
     *
     * @return Offset of the set, the default value is 0
     */
    int getOffset();

    /**
     * Limit of the set, if limit <= 0, means no limit
     *
     * @return total number of the set
     */
    int getLimit();
}
