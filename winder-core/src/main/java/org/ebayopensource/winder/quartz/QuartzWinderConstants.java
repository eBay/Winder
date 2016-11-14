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

/**
 * Winder Constants
 * 
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface QuartzWinderConstants {

    String TRIGGER_NAME_PREFIX = "Trigger-";
    String TRIGGER_GROUP_CRON = "CronTriggers";

    // Keys for job state map
    String KEY_JOBCLASS = "@jobclass";
    String KEY_JOBSTATUS = "@jobstatus";
    String KEY_JOBSTATUSMSG = "@jobstatusmsg";
    String KEY_JOBSTAGE = "@jobstage";
    String KEY_JOB_STEP = "@job_step";
    String KEY_JOBINPUT = "@jobinput";
    String KEY_JOBRESULT = "@jobresult";
    String KEY_JOBCREATEDATE = "@jobcreatedate";
    String KEY_JOBSTARTDATE = "@jobstartdate";
    String KEY_JOBENDDATE = "@jobenddate";
    String KEY_JOBOWNER = "@jobowner";
    String KEY_JOBTARGET = "@jobtarget";
    String KEY_JOBACTION = "@jobaction";
    String JOBSTATUSUPDATE_PREFIX = "@job/statusupdate";
    String KEY_STATUSUPDATECREATED = "created";
    String KEY_STATUSUPDATEMESSAGE = "statusmessage";
    String KEY_STATUSUPDATEEXECUTIONSTATUS = "executionstatus";

    String KEY_JOB_IS_AWAITING_FOR_ACTION = "@jobawaitingforaction";
    String JOB_ALERT_STATUS_PREFIX = "@job/alertstatus";
    String KEY_ALERT_STATUS_CREATED = "created";
    String KEY_ALERT_STATUS_MESSAGE = "alertmessage";
    String KEY_ALERT_STATUS_ACTION_TRIGGERED = "action";
    String KEY_ALERT_STATUS_USER_TRIGGERED = "user";

    String KEY_CHILDJOBS = "@jobchildren";
    String KEY_JOBPARENT = "@jobparent";

    String KEY_IS_REPLACE_JOB = "@jobisreplace";

    String KEY_TASKS = "@tasks";
    String KEY_TASK = "@tasks/%s";
    String KEY_TASK_NAME = "@tasks/%s/@name";
    String KEY_TASK_ACTION = "@tasks/%s/@action";
    String KEY_TASK_TARGET = "@tasks/%s/@target";
    String KEY_TASK_RESULT = "@tasks/%s/@result";
    String KEY_TASK_CREATED = "@tasks/%s/@created";
    String KEY_TASK_STARTTIME = "@tasks/%s/@starttime";
    String KEY_TASK_ENDTIME = "@tasks/%s/@endtime";
    String KEY_TASK_STATUS = "@tasks/%s/@status";
    String KEY_TASK_MANAGEDBY = "@tasks/%s/@managedBy";
    String KEY_TASK_STATUS_UPDATES_COUNT = "@tasks/%s/statuses/@count";
    String KEY_TASK_STATUS_UPDATES_PREFIX = "@tasks/%s/@statuses/%s";
    String KEY_COUNT = "count";
    String KEY_STATUSES = "statuses";

    String KEY_AUTO_PAUSE = "autoPause";

    static final int MAX_CHILD_JOB_SIZE = 1000;
    static final int MAX_CHILD_JOB_DETAILS = 50;
}
