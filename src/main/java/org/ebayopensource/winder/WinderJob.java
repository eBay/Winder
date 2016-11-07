package org.ebayopensource.winder;

/**
 * Common Interface of Job interface
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderJob {

    void execute(WinderJobContext ctx) throws WinderJobException;

}
