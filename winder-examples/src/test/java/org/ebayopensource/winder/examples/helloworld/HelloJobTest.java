package org.ebayopensource.winder.examples.helloworld;

import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.quartz.QuartzEngine;
import org.ebayopensource.winder.quartz.QuartzEngineInitializer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * How to run HelloJob
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public class HelloJobTest {

    @BeforeClass
    public static void init() {
        QuartzEngineInitializer.init();
    }

    @Test
    public void testJob() throws Exception {
        WinderEngine engine = QuartzEngine.getInstance();
        engine.scheduleJob(HelloJob.class);

        Thread.sleep(20000);
    }
}