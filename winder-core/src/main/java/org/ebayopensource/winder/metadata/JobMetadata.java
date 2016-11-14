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
package org.ebayopensource.winder.metadata;

import java.util.List;

/**
 * Job Metadata
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public interface JobMetadata {

    /**
     * Job type
     *
     * @return Job type
     */
    String getJobType();

    /**
     * Job group name
     *
     * @return Job Group name
     */
    String getJobGroup();

    /**
     * Return all steps
     *
     * @return all steps
     */
    List<StepMetadata> getSteps();

    /**
     * Return the first step
     *
     * @return the first step
     */
    StepMetadata getFirstStep();

    /**
     * Error handling step
     *
     * @return Error handling step
     */
    StepMetadata getErrorStep();

    /**
     * Return all 'DONE' steps
     *
     * @return all 'DONE' steps
     */
    List<StepMetadata> getDoneSteps();

    /**
     * Return step by given code
     *
     * @param code Code, if it is -1, it will return the first step of this job
     * @return Return step by given code
     */
    StepMetadata getStep(int code);
}
