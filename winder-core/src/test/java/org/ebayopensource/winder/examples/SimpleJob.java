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
package org.ebayopensource.winder.examples;

import org.ebayopensource.winder.*;
import org.ebayopensource.winder.anno.DoneStep;
import org.ebayopensource.winder.anno.FirstStep;
import org.ebayopensource.winder.anno.Job;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
@Job(type = "Test")
public enum SimpleJob implements Step<TaskInput, TaskResult, TaskContext<TaskInput, TaskResult>> {

    @FirstStep
    STEP1(10) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP1");
            String step2 = context.getTaskInput().getString("next_step", "STEP2");
            SimpleJob nextStep = SimpleJob.valueOf(step2);
            context.setCurrentStep(nextStep);
        }
    },

    STEP2(20) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP2");
            context.setCurrentStep(STEP3);
        }
    },

    STEP3(30) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP3");
            context.setCurrentStep(DONE);
        }
    },

    ERROR(40) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("ERROR");
        }
    },

    @DoneStep
    DONE(50) {
        @Override
        public void execute(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("DONE!");
        }
    };

    private final int code;

    public int code() {
        return code;
    }

    SimpleJob(final int code) {
        this.code = code;
    }

}
