package org.ebayopensource.winder;

/**
 * Job Id
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface JobId {

    String getName();

    String getGroup();

    String getCluster();

    String toString();
}
