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


/**
 * Task State
 *
 * Created by xshao on 9/16/16.
 */
public enum TaskState {
    /**
     * Executing or waiting
     *
     * That means it is still running in current step
     */
    WAITING,

    /**
     * To run next step
     */
    NEXT,

    /**
     * To run next group (go to the first step of one group)
     */
    NEXT_GROUP,

    /**
     * To skip the following step and jump to next next step
     */
    SKIP,

    /**
     * The step is completed
     */
    COMPLETED,

    /**
     * The job is on cancel pending
     */
    CANCEL_PENDING,

    /**
     * The job was cancelled
     */
    CANCELLED,

    /**
     * Current step is timed out
     */
    TIMEOUT,

    /**
     * Got error from current step
     */
    ERROR
}
