package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.UserAction;
import org.ebayopensource.winder.UserActionType;
import org.ebayopensource.winder.WinderEngine;
import org.quartz.JobDataMap;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;


/**
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzUserAction extends QuartzStatusBase<UserActionType> implements UserAction {

    public QuartzUserAction(WinderEngine engine, JobDataMap jobDataMap, String id) {
        super(engine, jobDataMap, id);
    }


    public QuartzUserAction(WinderEngine engine, JobDataMap jobDataMap, String id, UserActionType actionType, String message,
                            String owner) {
        super(engine, jobDataMap, id);

        String key = getKeyStatus();
        jobDataMap.put(key, actionType.name());

        key = getKeyMessage();
        jobDataMap.put(key, QuartzJobUtil.formatString(message, maxMessages, true));

        key = genKey(KEY_ALERT_STATUS_USER_TRIGGERED);
        jobDataMap.put(key, owner);
    }

    @Override
    public String getUser() {
        return jobDataMap.getString(genKey(KEY_ALERT_STATUS_USER_TRIGGERED));
    }

    @Override
    public UserActionType getType() {
        return getStatus(UserActionType.class);
    }

    @Override
    protected String getKeyDateCreated() {
        return KEY_ALERT_STATUS_CREATED;
    }

    @Override
    protected String getKeyMessage() {
        return KEY_ALERT_STATUS_MESSAGE;
    }

    @Override
    protected String getKeyStatus() {
        return KEY_ALERT_STATUS_ACTION_TRIGGERED;
    }
}
