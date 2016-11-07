package org.ebayopensource.winder;

/**
 * Winder Job Error Listener
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJobErrorListener<TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> {

    /**
     * Error Listener
     *
     * @param stepContext
     * @param currentStep
     */
    void onError(Step<TI, TR, C> currentStep, C stepContext);
}
