package org.ebayopensource.winder;

/**
 * Winder Job Detail Factory
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public interface WinderJobDetailFactory {

    /**
     * Create Winder Job Detail
     *
     * @param input TaskInput
     * @return TaskInput
     */
    WinderJobDetail createJobDetail(TaskInput input);
}
