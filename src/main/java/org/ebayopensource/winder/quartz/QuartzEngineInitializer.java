package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.WinderEngine;

/**
 * Quartz Engine Initializer for testing
 *
 * @author Sheldon Shao xshao@ebay.com on 10/20/16.
 * @version 1.0
 */
public class QuartzEngineInitializer {

    public static WinderEngine init() {
        WinderEngine quartzEngine = QuartzEngine.getInstance();
        if (quartzEngine == null) {
            QuartzEngine.setInstance(new QuartzEngine());
        }
        return QuartzEngine.getInstance();
    }
}
