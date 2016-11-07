package org.ebayopensource.winder;

import java.util.Date;

/**
 * Job Status Update
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface StatusUpdate {

    Date getDateCreated();

    String getDateCreatedAsString();

    String getMessage();

    StatusEnum getStatus();
}
