package org.ebayopensource.winder;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class WinderScheduleException extends WinderException {
    public WinderScheduleException(String msg) {
        super(msg);
    }

    public WinderScheduleException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
