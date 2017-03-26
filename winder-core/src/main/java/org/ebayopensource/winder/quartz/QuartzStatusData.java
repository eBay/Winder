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
import org.ebayopensource.winder.util.JsonUtil;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * Quartz Status Data
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzStatusData implements TaskStatusData {

    private String id;

    private JobDataMap map;

    private WinderEngine engine;

    private static Logger log = LoggerFactory.getLogger(QuartzStatusData.class);

    private int maxTaskResult = 0;

    private final String updateNamespace;

    public QuartzStatusData(WinderEngine engine, String id, JobDataMap map) {
        this.engine = engine;
        this.id = id;
        this.map = map;
        WinderConfiguration configuration = engine.getConfiguration();

        this.maxTaskResult = configuration.getInt("winder.task.maxTaskResult", 0);
        this.updateNamespace = QuartzJobUtil.generateKeyName(KEY_TASKS, QuartzJobUtil.generateKeyName(id, KEY_STATUSES));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return map.getString(String.format(KEY_TASK_NAME, id));
    }

    public void setName(String name) {
        map.put(String.format(KEY_TASK_NAME, id), name);
    }

    @Override
    public Date getDateCreated() {
        return engine.parseDateFromObject(map.get(String.format(KEY_TASK_CREATED, id)));
    }

    public void setDateCreated(Date created) {
        map.put(String.format(KEY_TASK_CREATED, id), created.getTime());
    }

    @Override
    public Date getStartTime() {
        return engine.parseDateFromObject(map.get(String.format(KEY_TASK_STARTTIME, id)));
    }

    @Override
    public void setStartTime(Date startTime) {
        map.put(String.format(KEY_TASK_STARTTIME, id), startTime.getTime());
    }

    @Override
    public Date getEndTime() {
        return engine.parseDateFromObject(map.get(String.format(KEY_TASK_ENDTIME, id)));
    }

    @Override
    public void setEndTime(Date endTime) {
        map.put(String.format(KEY_TASK_ENDTIME, id), endTime.getTime());
    }

    @Override
    public StatusEnum getExecutionStatus() {
        StatusEnum result = StatusEnum.UNKNOWN;
        String text = map.getString(String.format(KEY_TASK_STATUS, id));
        if (text != null && !text.isEmpty()) {
            try {
                result = StatusEnum.valueOf(text);
            } catch (RuntimeException e) {
                log.error("Error parsing execution status from " + text, e);
            }
        }
        return result;
    }

    @Override
    public void setExecutionStatus(StatusEnum executionStatus) {
        map.put(String.format(KEY_TASK_STATUS, id), executionStatus.name());
    }

    @Override
    public String getSessionId() {
        return map.getString(String.format(KEY_TASK_SESSION_ID, id));
    }

    @Override
    public void setSessionId(String sessionId) {
        map.put(String.format(KEY_TASK_SESSION_ID, id), sessionId);
    }

    @Override
    public String getTarget() {
        return map.getString(String.format(KEY_TASK_TARGET, id));
    }

    @Override
    public void setTarget(String target) {
        map.put(String.format(KEY_TASK_TARGET, id), target);
    }

    @Override
    public String getAction() {
        return map.getString(String.format(KEY_TASK_ACTION, id));
    }

    @Override
    public void setAction(String action) {
        map.put(String.format(KEY_TASK_ACTION, id), action);
    }

    @Override
    public Parameters<Object> getResult() {
        String text = map.getString(String.format(KEY_TASK_RESULT, id));
        try {
            return JsonUtil.jsonToParameters(text);
        } catch (IOException e) {
            log.error("Error parsing execution status from " + text, e);
            return new ParametersMap<>();
        }
    }

    @Override
    public void setResult(Parameters<Object> result) {
        String str = null;
        try {
            str = JsonUtil.writeValueAsString(result);
        } catch (IOException e) {
            log.error("Convert value as string error " + result, e);
        }
        if (str != null && maxTaskResult > 0 && str.length() > maxTaskResult) {
            throw new IllegalArgumentException("Task result map exceeds max (" +
                    maxTaskResult + "), internal code error");
        }
        map.put(String.format(KEY_TASK_RESULT, id), str);
    }

    @Override
    public StatusUpdate addUpdate(StatusEnum executionStatus, String statusMessage) {
        return QuartzJobUtil.addOrGetUpdate(engine, updateNamespace, map, executionStatus, statusMessage);
    }

    @Override
    public List<StatusUpdate> getUpdates() {
        return QuartzJobUtil.getAllStatus(QuartzStatusUpdate.class, engine, updateNamespace, map);
    }
}
