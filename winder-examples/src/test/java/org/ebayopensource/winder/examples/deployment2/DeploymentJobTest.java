package org.ebayopensource.winder.examples.deployment2;

import org.ebayopensource.deployment.SimpleDeploymentAPI;
import org.ebayopensource.deployment.SimpleGroupStrategy;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.WinderTaskInput;
import org.ebayopensource.winder.examples.WinderTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for DeploymentJob
 *
 * @author Sheldon Shao xshao@ebay.com on 11/28/16.
 * @version 1.0
 */
public class DeploymentJobTest extends WinderTest {

    @Before
    public void start() {
        super.start();

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
}