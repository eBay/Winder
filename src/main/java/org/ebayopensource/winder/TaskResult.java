package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;

/**
 * Winder Job Result
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public interface TaskResult extends Parameters<Object> {

    /**
     * To Json
     *
     * @return
     */
    String toJson();
}
