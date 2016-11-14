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
