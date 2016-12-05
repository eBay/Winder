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

import java.util.List;

/**
 * Software Deployment API
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
public interface DeploymentAPI {

    /**
     * Precheck all the instances
     *
     * @param fqdns fully qualified domain name
     * @return The instance object with state of the targets
     */
    List<InstanceState> preCheck(List<String> fqdns);
    
    /**
     * Download specific software on the target servers
     *
     * @param instances Instance states
     * @param software given software and version
     * @return the same instance states
     */
    List<InstanceState> download(List<InstanceState> instances, String software);

    /**
     * Validate downloading/startup
     *
     * @param states Instance states
     * @param step The step name to validate
     * @return progress, 0-100 for each target. 100 means the task is done.  -1 means the task was failed
     */
    List<Integer> validate(List<InstanceState> states, String step);

    /**
     * Start the software on target servers
     *
     * @param states Instance states
     * @param software Software
     * @return the same instance states
     */
    List<InstanceState> startup(List<InstanceState> states, String software);

}
