package org.ebayopensource.winder.quartz;

import org.ebayopensource.common.util.PropertyParameters;
import org.ebayopensource.winder.WinderConfiguration;

import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzConfiguration extends PropertyParameters implements WinderConfiguration {

    public QuartzConfiguration(Properties properties) {
        super(properties);
    }

    public QuartzConfiguration() {
        this(System.getProperties());
    }

    private TimeZone timeZone = null;

    @Override
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            String timeZoneId = getString("winder.timezone");
            if (timeZoneId != null) {
                timeZone = TimeZone.getTimeZone(timeZoneId);
            }
            if (timeZone == null) {
                timeZone = TimeZone.getDefault();
            }
        }
        return timeZone;
    }
}
