package org.ebayopensource.winder.quartz;

import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.SchedulerStateRecord;

/**
 * Fix for instance recovering issue
 *
 * @author Sheldon Shao xshao@ebay.com on 10/18/16.
 * @version 1.0
 */
public class WinderJobStoreTx extends JobStoreTX {

    protected long calcFailedIfAfter(SchedulerStateRecord rec) {
        return rec.getCheckinTimestamp() + Math.max(rec.getCheckinInterval() * 2,
                (System.currentTimeMillis() - lastCheckin)) + rec.getCheckinInterval() * 2;
    }
}
