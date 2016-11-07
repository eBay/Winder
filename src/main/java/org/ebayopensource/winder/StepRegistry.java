package org.ebayopensource.winder;

/**
 * Step Registry
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface StepRegistry {

    Step lookup(Class<? extends Step> clazz, final int code);

    void register(Class<? extends Step> clazz);

    String getJobType(Class<? extends Step> clazz);

    Step getFirstStep(Class<? extends Step> clazz);

    Step getErrorStep(Class<? extends Step> clazz);

    Step[] getDoneSteps(Class<? extends Step> clazz);
}
