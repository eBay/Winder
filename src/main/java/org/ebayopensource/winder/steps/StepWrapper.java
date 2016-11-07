package org.ebayopensource.winder.steps;

import org.ebayopensource.winder.Step;
import org.ebayopensource.winder.TaskContext;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.TaskResult;

/**
 * Step Wrapper
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class StepWrapper <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> implements Step<TI, TR, C>{

    protected Step<TI, TR, C> step;

    public StepWrapper(Step<TI, TR, C> step) {
        this.step = step;
    }

    @Override
    public String name() {
        return step.name();
    }

    @Override
    public int code() {
        return step.code();
    }

    @Override
    public void process(C context) throws Exception {
        step.process(context);
    }
}
