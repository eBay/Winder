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
 * Winder Step Metadata implementation
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public class WinderStepMetadata implements StepMetadata {

    private String name;
    private boolean first;
    private boolean error;
    private boolean done;
    private StatusEnum finalStatus = StatusEnum.UNKNOWN;
    private transient Step step;
    private int code;
    private boolean repeatable = true;

    public WinderStepMetadata(String name, Step step) {
        setStep(step);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    @Override
    public boolean isError() {
        return error;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public StatusEnum getFinalStatus() {
        return finalStatus;
    }

    @Override
    public Step toStep() {
        return step;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setFinalStatus(StatusEnum finalStatus) {
        this.finalStatus = finalStatus;
    }

    public void setStep(Step step) {
        this.step = step;
        this.name = step.name();
        this.code = step.code();
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
}
