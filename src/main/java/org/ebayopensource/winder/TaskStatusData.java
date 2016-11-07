package org.ebayopensource.winder;

import org.ebayopensource.common.util.Parameters;

import java.util.Date;
import java.util.List;

/**
 * Task Status Data
 *
 * @author Sheldon Shao xshao@ebay.com on 10/16/16.
 * @version 1.0
 */
public interface TaskStatusData {


    String getId();

    String getManagedBy();

    void setManagedBy(String managedBy);

    String getName();

//    void setName(String type);

    Date getDateCreated();

//    void setDateCreated(Date created);

    Date getStartTime();

    void setStartTime(Date startTime);

    Date getEndTime();

    void setEndTime(Date endTime);

    StatusEnum getExecutionStatus();

    void setExecutionStatus(StatusEnum executionStatus);

    String getTarget();

    void setTarget(String target);

    String getAction();

    void setAction(String action);

    Parameters<Object> getResult();

    void setResult(Parameters<Object> result);

    StatusUpdate addUpdate(StatusEnum executionStatus, String statusMessage);

    List<StatusUpdate> getUpdates();
}
