package org.ebayopensource.winder;

import java.util.Date;

/**
 * User action,
 *
 * User may need to interact the job, for example, pause, cancel, resume etc.
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface UserAction {

    Date getDateCreated();

    String getDateCreatedAsString();

    String getMessage();

//    void setMessage(String message);

    String getUser();

//    void setUser(String user);

    UserActionType getType();

//    void setType(InteractionType type);
}
