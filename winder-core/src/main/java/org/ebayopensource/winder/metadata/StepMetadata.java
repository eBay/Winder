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
