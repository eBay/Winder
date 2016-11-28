package org.ebayopensource.winder.examples.deployment1;

import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.WinderTaskInput;
import org.ebayopensource.winder.examples.helloworld.HelloJob;
import org.ebayopensource.winder.quartz.QuartzEngineInitializer;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Test case for DeploymentJob
 *
 * @author Sheldon Shao xshao@ebay.com on 11/28/16.
 * @version 1.0
 */
public class DeploymentJobTest {

    private static WinderEngine engine;

    @BeforeClass
    public static void start() {
        engine = QuartzEngineInitializer.start();
    }

    @Test
    public void testJob() throws Exception {
        TaskInput input = new WinderTaskInput(DeploymentJob.class);
        input.setJobOwner("Sheldon");
        input.put("targets", new String[] { "Host1", "Host2", "Host3"});
        input.put("software", "Helloworld-1.0.0");

        engine.scheduleJob(input);

        Thread.sleep(10000);
    }

    @AfterClass
    public static void stop() {
        if (engine != null) {
            engine.stop();
        }
    }
}