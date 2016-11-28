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
package org.ebayopensource.deployment;

import org.ebayopensource.winder.StatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Deploy API
 *
 * A real deployment API should call some existing restful API to manage the instance.
 * This implementation just shows the work flow.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
public class SimpleDeploymentAPI implements DeploymentAPI {
    @Override
    public List<InstanceState> preCheck(List<String> fqdns) {
        List<InstanceState> instances = new ArrayList<>(fqdns.size());
        for(int i = 0; i < fqdns.size(); i ++) {
            String fqdn = fqdns.get(i);
            System.out.println("Instance:" + fqdn + " " + StatusEnum.EXECUTING);
            instances.add(new InstanceState(fqdn, StatusEnum.EXECUTING));
        }
        return instances;
    }

    @Override
    public List<InstanceState> download(List<InstanceState> instances, String software) {
        int size = instances.size();
        for(int i = 0; i < size; i ++) {
            InstanceState instance = instances.get(i);
            if (instance.getCode() == StatusEnum.EXECUTING) {
                //Call downloading API
                System.out.println("Downloading:" + software + " in " + instance.getFqdn());

                instance.setSessionId("SESSION_ID_DOWNLOAD_" + i);
            }
        }
        return instances;
    }

    @Override
    public List<Integer> validate(List<InstanceState> instances, String step) {
        //Wait until the task was done.
        int size = instances.size();
        List<Integer> result = new ArrayList<>(size);
        for(int i = 0; i < size; i ++) {
            InstanceState instance = instances.get(i);
            if (instance.getCode() == StatusEnum.EXECUTING) {
                ////Call validating API
                System.out.println("Validating " + step + " " + instance.getSessionId() + " in " + instance.getFqdn());
                result.add(100);
            }
        }
        return result;
    }

    @Override
    public List<InstanceState> startup(List<InstanceState> instances, String software) {
        int size = instances.size();
        for(int i = 0; i < size; i ++) {
            InstanceState instance = instances.get(i);
            if (instance.getCode() == StatusEnum.EXECUTING) {
                System.out.println("Starting:" + software + " in " + instance.getFqdn());

                //Just a sample, typically it should be an UUID.
                instance.setSessionId("SESSION_ID_STARTUP_" + i);
            }
        }
        return instances;
    }
}
