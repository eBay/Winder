/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 * <p>
 * Licensed under the MIT license.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * <p>
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.examples;

import org.ebayopensource.common.config.PropertyUtil;
import org.ebayopensource.common.util.PropertyParameters;
import org.ebayopensource.deployment.SimpleDeploymentAPI;
import org.ebayopensource.deployment.SimpleGroupStrategy;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.WinderTaskInput;
import org.ebayopensource.winder.examples.deployment2.DeploymentJob;
import org.ebayopensource.winder.quartz.QuartzConfiguration;
import org.ebayopensource.winder.quartz.QuartzEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Sheldon Shao xshao@ebay.com on 3/26/17.
 * @version 1.0
 */
@Ignore
public class WinderMysqlTest {

    protected WinderEngine engine;

    @Before
    public void start() {
        QuartzConfiguration configuration = new QuartzConfiguration();
        Properties properties = PropertyUtil.getResourceAsProperties("winder.properties");
        PropertyParameters parameters = new PropertyParameters(properties);
        configuration.putAll(parameters);



        configuration.put("deployment_api", new SimpleDeploymentAPI());
        configuration.put("group_strategy", new SimpleGroupStrategy());

        engine = new QuartzEngine(configuration);
        engine.start();
    }

    @After
    public void stop() {
        if (engine != null) {
            engine.stop();
        }
    }

    @Test
    @Ignore
    public void testJob() throws Exception {
        TaskInput input = new WinderTaskInput(DeploymentJob.class);
        input.setJobOwner("Sheldon");
        input.put("targets", new String[] { "Host0", "Host1", "Host2", "Host3", "Host4", "Host5", "Host6", "Host7", "Host8", "Host9"});
        input.put("software", "Helloworld-3.0.0");

        engine.scheduleJob(input);

        Thread.sleep(10000);
    }
}
