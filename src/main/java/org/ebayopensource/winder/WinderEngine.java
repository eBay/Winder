package org.ebayopensource.winder;

import java.util.Date;

/**
 * Winder Runtime Context
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface WinderEngine {

    /**
     * Return cluster type
     *
     * @return The quartz cluser type
     */
    String getClusterName();

    /**
     * Use default DateFormat to format
     * @param date
     * @return
     */
    String formatDate(Date date);

    /**
     * "yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    String formatShortDate(Date date);

    Date parseDateFromString(String str);

    /**
     * Return configuration
     *
     * @return Configuration
     */
    WinderConfiguration getConfiguration();

    /**
     * Winder Job Detail Factory
     *
     * @return Job Detail Factory
     */
    WinderJobDetailFactory getJobDetailFactory();

    /**
     * Return SchedulerManager
     *
     * @return SchedulerManager
     */
    WinderSchedulerManager getSchedulerManager();


    /**
     * Fast method
     *
     * @param jobClass Job class
     * @throws WinderScheduleException
     */
    void scheduleJob(Class jobClass) throws WinderScheduleException;

    /**
     * Fast method
     *
     * @param taskInput TaskInput
     * @throws WinderScheduleException
     */
    <TI extends TaskInput> void scheduleJob(TI taskInput) throws WinderScheduleException;

    /**
     * Job Detail Merger
     *
     * @return Job Detail Merger
     */
    WinderJobDetailMerger getJobDetailMerger();

    /**
     * Job Error Listener
     *
     * @return Job Error Listener
     */
    <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> WinderJobErrorListener<TI, TR, C> getJobErrorListener(C taskContext);

    /**
     * Step Registry
     *
     * @return
     */
    StepRegistry getStepRegistry();
}
