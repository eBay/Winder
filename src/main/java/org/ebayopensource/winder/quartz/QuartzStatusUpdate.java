package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.StatusEnum;
import org.ebayopensource.winder.StatusUpdate;
import org.ebayopensource.winder.WinderEngine;
import org.quartz.JobDataMap;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;

/**
 * Status Update
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public class QuartzStatusUpdate extends QuartzStatusBase<StatusEnum> implements StatusUpdate {

    public QuartzStatusUpdate(WinderEngine engine, JobDataMap jobDataMap, String id, StatusEnum statusEnum, String message) {
        this(engine, jobDataMap, id);

        String key = genKey(KEY_STATUSUPDATEEXECUTIONSTATUS);
        jobDataMap.put(key, statusEnum.name());

        key = genKey(KEY_STATUSUPDATEMESSAGE);
        jobDataMap.put(key, QuartzJobUtil.formatString(message, maxMessages, true));
    }

    public QuartzStatusUpdate(WinderEngine engine, JobDataMap jobDataMap, String id) {
        super(engine, jobDataMap, id);
    }

    @Override
    protected String getKeyDateCreated() {
        return KEY_STATUSUPDATECREATED;
    }

    @Override
    protected String getKeyMessage() {
        return KEY_STATUSUPDATEMESSAGE;
    }

    @Override
    protected String getKeyStatus() {
        return KEY_STATUSUPDATEEXECUTIONSTATUS;
    }

    @Override
    public StatusEnum getStatus() {
        return super.getStatus(StatusEnum.class);
    }
}
