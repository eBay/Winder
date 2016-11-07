package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.*;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Quartz Implementation
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class QuartzEngine implements WinderEngine {

    private String clusterName;
    private WinderStepRegistry stepRegistry = new WinderStepRegistry();

    static final String DATE_FORMAT_STR = "yyyy-MM-dd-HH:mm:ss.SSS'z'Z";  // use GMT-7
    static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";  // use GMT-7

    private static Logger log = LoggerFactory.getLogger(QuartzEngine.class);

    private WinderConfiguration configuration;

    private WinderSchedulerManager schedulerManager;

    private WinderJobDetailFactory jobDetailFactory;

    private QuartzJobDetailMerger jobDetailMerger = new QuartzJobDetailMerger();

    private static WinderEngine instance;

    public static WinderEngine getInstance() {
        return instance;
    }

    protected static void setInstance(WinderEngine engine) {
        instance = engine;
    }

    public QuartzEngine(Properties properties) {
        this(new QuartzConfiguration(properties));
        this.jobDetailFactory = new QuartzJobDetailFactory(this);
        QuartzInitializer initializer = new QuartzInitializer();
        Scheduler scheduler = initializer.init(this);
        this.schedulerManager = new QuartzSchedulerManager(this, scheduler);
        instance = this;
    }

    //For extension
    protected QuartzEngine(WinderConfiguration configuration) {
        this.configuration = configuration;
        setClusterName(configuration.getString("winder.cluster", "winder"));
    }

    public QuartzEngine() {
        this(System.getProperties());
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

    @Override
    public String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_STR);
        df.setTimeZone(configuration.getTimeZone());
        return df.format(date);
    }

    /**
     * "yyyy-MM-dd"
     *
     * @param date
     * @return
     */
    public String formatShortDate(Date date) {
        DateFormat df = new SimpleDateFormat(SHORT_DATE_FORMAT);
        df.setTimeZone(configuration.getTimeZone());
        return df.format(date);
    }

    @Override
    public Date parseDateFromString(String s) {
        if (s == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_STR);
        df.setTimeZone(configuration.getTimeZone());
        Date result = null;
        try {
            result = df.parse(s);
        } catch (Exception e) {
            log.warn("Error parsing date " + s, e);
        }
        return result;
    }


    @Override
    public WinderConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public WinderJobDetailFactory getJobDetailFactory() {
        return jobDetailFactory;
    }

    @Override
    public WinderSchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    @Override
    public void scheduleJob(Class jobClass) throws WinderScheduleException {
        scheduleJob(new WinderTaskInput(jobClass));
    }

    @Override
    public <TI extends TaskInput> void scheduleJob(TI taskInput) throws WinderScheduleException {
        getSchedulerManager().scheduleJob(taskInput);
    }

    @Override
    public WinderJobDetailMerger getJobDetailMerger() {
        return jobDetailMerger;
    }

    @Override
    public <TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> WinderJobErrorListener<TI, TR, C> getJobErrorListener(C taskContext) {
        return null;
    }

    @Override
    public StepRegistry getStepRegistry() {
        return stepRegistry;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setConfiguration(WinderConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setSchedulerManager(WinderSchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    public void setJobDetailFactory(WinderJobDetailFactory jobDetailFactory) {
        this.jobDetailFactory = jobDetailFactory;
    }

    public void setJobDetailMerger(QuartzJobDetailMerger jobDetailMerger) {
        this.jobDetailMerger = jobDetailMerger;
    }
}
