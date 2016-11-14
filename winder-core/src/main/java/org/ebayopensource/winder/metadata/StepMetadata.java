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

import org.ebayopensource.winder.StatusEnum;
import org.ebayopensource.winder.Step;

/**
 * Step Metadata
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public interface StepMetadata {

    /**
     * Return step name
     *
     * @return step name
     */
    String getName();

    /**
     * Return the code of the step
     *
     * @return code
     */
    int getCode();

    /**
     * Whether the step is first or not
     *
     * @return if it is first step
     */
    boolean isFirst();

    /**
     * Error handling step
     *
     * @return if the step is to handle error
     */
    boolean isError();

    /**
     * After this step, the job will be quit.
     *
     * @return After this step, the job will be quit.
     */
    boolean isDone();

    /**
     * Is the step repeatable ? By default it is true
     *
     * @return Is the step repeatable
     */
    boolean isRepeatable();

    /**
     * The final status is to set the status for the job when the step is done.
     * This field is only available when it is error or done.
     * When the value is UNKNOWN, it won't set the status.
     *
     * @return the default is StatusEnum.UNKNOWN
     */
    StatusEnum getFinalStatus();


    /**
     * Find step by metadata
     *
     * @return find the step by metadata
     */
    Step toStep();
}
