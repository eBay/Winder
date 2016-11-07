package org.ebayopensource.winder;

/**
 * Exception from job
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class WinderJobException extends WinderException {
    public WinderJobException(String msg) {
        super(msg);
    }

    public WinderJobException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
