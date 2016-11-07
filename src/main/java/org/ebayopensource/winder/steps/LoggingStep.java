package org.ebayopensource.winder.steps;

import org.ebayopensource.winder.Step;
import org.ebayopensource.winder.TaskContext;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Step
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class LoggingStep <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> extends StepWrapper<TI, TR, C> {

    public LoggingStep(Step<TI, TR, C> step) {
        super(step);
    }

    @Override
    public void process(C ctx) throws Exception {
        Logger log = LoggerFactory.getLogger(ctx.getClass());

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "JobId: %s Group: %s Entering Stage: %s",
                    ctx.getJobIdAsString(), ctx.getGroupId(), this.name()));
        }

        step.process(ctx);

        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "JobId: %s Group: %s Exiting Stage %s, next stage set to %s",
                    ctx.getJobIdAsString(), ctx.getGroupId(), this.name(), ctx.getCurrentStep().name()));
        }
    }

}