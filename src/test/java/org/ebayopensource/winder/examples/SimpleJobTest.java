package org.ebayopensource.winder.examples;

import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.quartz.QuartzEngine;
import org.ebayopensource.winder.quartz.QuartzEngineInitializer;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple Job Test
 *
 * @author Sheldon Shao xshao@ebay.com on 10/20/16.
 * @version 1.0
 */
public class SimpleJobTest {

    @BeforeClass
    public static void init() {
        QuartzEngineInitializer.init();
    }

    @Test
    public void testJob() throws Exception {
        WinderEngine engine = QuartzEngine.getInstance();
        engine.scheduleJob(SimpleJob.class);

        Thread.sleep(60000);
    }
}
