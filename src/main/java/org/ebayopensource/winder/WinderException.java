package org.ebayopensource.winder;

/**
 * Winder Exception
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class WinderException extends Exception {

    public WinderException(String msg) {
        super(msg);
    }

    public WinderException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
