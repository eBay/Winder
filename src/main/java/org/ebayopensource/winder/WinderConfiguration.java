package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;

import java.util.TimeZone;

/**
 * Winder Configuration
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderConfiguration extends Parameters<Object> {

    TimeZone getTimeZone();
}
