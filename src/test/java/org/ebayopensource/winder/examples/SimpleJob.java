package org.ebayopensource.winder.examples;

import org.ebayopensource.winder.*;
import org.ebayopensource.winder.anno.Job;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
@Job(
        type = "Test",

        firstStep = "STEP1",

        doneSteps = {"ERROR", "DONE"}
)
public enum SimpleJob implements Step<TaskInput, TaskResult, TaskContext<TaskInput, TaskResult>> {

    STEP1(10) {
        @Override
        public void process(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP1");
            String step2 = context.getTaskInput().getString("next_step", "STEP2");
            SimpleJob nextStep = SimpleJob.valueOf(step2);
            context.setCurrentStep(nextStep);
        }
    },

    STEP2(20) {
        @Override
        public void process(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP2");
            context.setCurrentStep(STEP3);
        }
    },

    STEP3(30) {
        @Override
        public void process(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("STEP3");
            context.setCurrentStep(DONE);
        }
    },

    ERROR(40) {
        @Override
        public void process(TaskContext<TaskInput, TaskResult> context) throws Exception {
            System.out.println("ERROR");
        }
    },
    DONE(50) {
        @Override
        public void process(TaskContext<TaskInput, TaskResult> context) throws Exception {
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
