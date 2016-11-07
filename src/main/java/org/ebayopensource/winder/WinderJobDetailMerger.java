package org.ebayopensource.winder;

/**
 * Merge two job details, the DB details might be changed from outside when the job is running
 * So the running job should merge those runtime change into current runtime job detail.
 * The interface is to make the merge customizable.
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJobDetailMerger {

    /**
     * Merge two details
     *
     * @param dbDetail Source
     * @param runtimeDetail Target
     * @return New Target
     */
    WinderJobDetail merge(WinderJobDetail dbDetail, WinderJobDetail runtimeDetail);
}
