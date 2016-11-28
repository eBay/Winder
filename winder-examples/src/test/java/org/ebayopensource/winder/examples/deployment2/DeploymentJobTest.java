package org.ebayopensource.winder.examples.deployment2;

import org.ebayopensource.deployment.SimpleDeploymentAPI;
import org.ebayopensource.deployment.SimpleGroupStrategy;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.WinderTaskInput;
import org.ebayopensource.winder.quartz.QuartzEngineInitializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

        engine.getConfiguration().put("deployment_api", new SimpleDeploymentAPI());
        engine.getConfiguration().put("group_strategy", new SimpleGroupStrategy());
    }

    @Test
    public void testJob() throws Exception {
        TaskInput input = new WinderTaskInput(DeploymentJob.class);
        input.setJobOwner("Sheldon");
        input.put("targets", new String[] { "Host0", "Host1", "Host2", "Host3", "Host4", "Host5", "Host6", "Host7", "Host8", "Host9"});
        input.put("software", "Helloworld-3.0.0");

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