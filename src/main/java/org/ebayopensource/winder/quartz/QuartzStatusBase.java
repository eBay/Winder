package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.WinderEngine;
import org.quartz.JobDataMap;

import java.util.Date;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.KEY_STATUSUPDATECREATED;

/**
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public abstract class QuartzStatusBase<S extends Enum> {

    protected transient Date dateCreated;

    protected WinderEngine engine;

    protected JobDataMap jobDataMap;

    protected String id;

    protected int maxMessages;

    public QuartzStatusBase(WinderEngine engine, JobDataMap jobDataMap, String id) {
        this.engine = engine;
        this.jobDataMap = jobDataMap;
        this.id = id;
        String key = genKey(getKeyDateCreated());
        if (!jobDataMap.containsKey(key)) {
            dateCreated = new Date();
            jobDataMap.put(key, engine.formatDate(dateCreated));
        }
        this.maxMessages = engine.getConfiguration().getInt("winder.task.maxMessage", 1000);
    }

    protected abstract String getKeyDateCreated();

    protected abstract String getKeyMessage();

    protected abstract String getKeyStatus();

    protected String genKey(String key) {
        return QuartzJobUtil.generateKeyName(id, key);
    }

    public Date getDateCreated() {
        if (dateCreated == null) {
            dateCreated = engine.parseDateFromString(getDateCreatedAsString());
        }
        return dateCreated;
    }

    public String getDateCreatedAsString() {
        return jobDataMap.getString(genKey(getDateCreatedAsString()));
    }

    public String getMessage() {
        return jobDataMap.getString(genKey(getKeyMessage()));
    }

    protected S getStatus(Class<S> clazz) {
        String key = genKey(getKeyStatus());
        String value = jobDataMap.getString(key);
        if (value != null) {
            try {
                return (S)Enum.valueOf(clazz, value.toUpperCase());
            } catch (Exception ex) {
            }
        }
        return (S)Enum.valueOf(clazz, "UNKNOWN");
    }
}
