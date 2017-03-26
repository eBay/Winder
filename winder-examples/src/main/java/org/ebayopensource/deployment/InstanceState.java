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
import org.ebayopensource.winder.TaskStatusData;

import java.io.Serializable;

/**
 * Instance State
 *
 * @author Sheldon Shao xshao@ebay.com on 11/27/16.
 * @version 1.0
 */
public class InstanceState implements Serializable {

    private String fqdn;

    private StatusEnum code = StatusEnum.UNKNOWN;

    private String sessionId;

    private transient TaskStatusData statusData;

    public InstanceState(String fqdn, StatusEnum code) {
        this.fqdn = fqdn;
        this.code = code;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public StatusEnum getCode() {
        return code;
    }

    public void setCode(StatusEnum code) {
        this.code = code;
        if (statusData != null) {
            statusData.setExecutionStatus(code);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        if (statusData != null) {
            statusData.setSessionId(sessionId);
        }
    }

    public InstanceState(TaskStatusData taskStatusData) {
        this.fqdn = taskStatusData.getTarget();
        this.code = taskStatusData.getExecutionStatus();
        this.sessionId = taskStatusData.getSessionId();
        this.statusData = taskStatusData;
    }

    public TaskStatusData getStatusData() {
        return statusData;
    }
}
