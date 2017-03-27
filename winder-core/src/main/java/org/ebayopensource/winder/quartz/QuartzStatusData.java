/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.quartz;

import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.common.util.ParametersMap;
import org.ebayopensource.winder.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * Quartz Status Data
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzStatusData implements TaskStatusData {

    private Parameters<Object> parameters;

    private WinderEngine engine;

    public QuartzStatusData(WinderEngine engine, Parameters<Object> parameters) {
        this.engine = engine;
        this.parameters = parameters;
    }

    public void setId(String id) {
        parameters.put(KEY_TASK_ID, id);
    }

    @Override
    public String getId() {
        return parameters.getString(KEY_TASK_ID);
    }

    @Override
    public String getName() {
        return parameters.getString(KEY_TASK_NAME);
    }

    public void setName(String name) {
        parameters.put(KEY_TASK_NAME, name);
    }

    @Override
    public Date getDateCreated() {
        return parameters.getDate(KEY_TASK_DATE_CREATED);
    }

    public void setDateCreated(Date created) {
        parameters.put(KEY_TASK_DATE_CREATED, created.getTime());
    }

    @Override
    public Date getStartDate() {
        return parameters.getDate(KEY_TASK_START_DATE);
    }

    @Override
    public void setStartDate(Date startTime) {
        parameters.put(KEY_TASK_START_DATE, startTime.getTime());
    }

    @Override
    public Date getEndDate() {
        return parameters.getDate(KEY_TASK_END_DATE);
    }

    @Override
    public void setEndDate(Date endTime) {
        parameters.put(KEY_TASK_END_DATE, endTime.getTime());
    }

    @Override
    public StatusEnum getExecutionStatus() {
        return parameters.getEnum(StatusEnum.class, KEY_TASK_STATUS, StatusEnum.UNKNOWN);
    }

    @Override
    public void setExecutionStatus(StatusEnum executionStatus) {
        parameters.put(KEY_TASK_STATUS, executionStatus.name());
    }

    @Override
    public String getSessionId() {
        return parameters.getString(KEY_TASK_SESSION_ID);
    }

    @Override
    public void setSessionId(String sessionId) {
        parameters.put(KEY_TASK_SESSION_ID, sessionId);
    }

    @Override
    public String getTarget() {
        return parameters.getString(KEY_TASK_TARGET);
    }

    @Override
    public void setTarget(String target) {
        parameters.put(KEY_TASK_TARGET, target);
    }

    @Override
    public String getAction() {
        return parameters.getString(KEY_TASK_ACTION);
    }

    @Override
    public void setAction(String action) {
        parameters.put(KEY_TASK_ACTION, action);
    }

    @Override
    public Parameters<Object> getResult() {
        return parameters.getParameters(KEY_TASK_RESULT);
    }

    @Override
    public void setResult(Parameters<Object> result) {
        parameters.put(KEY_TASK_RESULT, result.toMap());
    }

    protected List<Map> getListOfMap(String key) {
        List<Map> list = parameters.getList(key);
        if (list == null) {
            list = new ArrayList<>(5);
            parameters.put(key, list);
        }
        return list;
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum executionStatus, String statusMessage) {
        QuartzStatusUpdate update = new QuartzStatusUpdate(engine, new ParametersMap<>(),
                executionStatus, statusMessage);

        getListOfMap(DATA_STATUS_UPDATES).add(update.toParameters().toMap());
        return update;
    }

    @Override
    public List<StatusUpdate> getUpdates() {
        List<Map> list = getListOfMap(DATA_STATUS_UPDATES);
        List<StatusUpdate> statusUpdates = new ArrayList<>(list.size());
        for(Map m: list) {
            statusUpdates.add(new QuartzStatusUpdate(engine, new ParametersMap<>(m)));
        }
        return statusUpdates;
    }

    public Map<String, Object> toMap() {
        return parameters.toMap();
    }
}
