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
    String KEY_JOB_CLASS = "job_class";
    String KEY_JOB_STATUS = "job_status";
    String KEY_JOB_STATUS_MESSAGE = "job_status_message";
    String KEY_JOB_STEP = "job_step";
    String KEY_JOB_INPUT = "job_input";
    String KEY_JOB_RESULT = "job_result";
    String KEY_JOB_DATE_CREATED = "job_date_created";
    String KEY_JOB_START_DATE = "job_start_date";
    String KEY_JOB_END_DATE = "job_end_date";
    String KEY_JOB_OWNER = "job_owner";
    String KEY_JOB_TARGET = "job_target";
    String KEY_JOB_ACTION = "job_action";
    String KEY_JOB_ID = "job_id";

    String KEY_JOB_IS_AWAITING_FOR_ACTION = "job_awaiting_for_action";

    String KEY_USER_ACTIONS = "job_user_actions";
    String KEY_STATUS_UPDATES = "job_status_updates";

    String KEY_DATE_CREATED = "created";
    String KEY_MESSAGE = "message";
    String KEY_EXECUTION_STATUS = "execution_status";
    String KEY_ACTION = "action";
    String KEY_OWNER = "owner";

    String DATA_STATUS_UPDATES = "status_updates";


    String KEY_CHILD_JOBS = "job_children";
    String KEY_JOB_PARENT = "job_parent";

    String KEY_IS_REPLACE_JOB = "job_is_replace";

    String KEY_TASKS = "tasks";
    String KEY_TASK_ID = "id";
    String KEY_TASK_NAME = "name";

    String KEY_TASK_ACTION = "action";
    String KEY_TASK_TARGET = "target";
    String KEY_TASK_DATE_CREATED = "date_created";
    String KEY_TASK_START_DATE = "start_date";
    String KEY_TASK_END_DATE = "end_date";
    String KEY_TASK_STATUS = "status";

    String KEY_AUTO_PAUSE = "auto_pause";

    static final int MAX_CHILD_JOB_SIZE = 1000;
    static final int MAX_CHILD_JOB_DETAILS = 50;
}
