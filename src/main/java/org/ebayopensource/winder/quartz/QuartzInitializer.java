package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.WinderConfiguration;
import org.ebayopensource.winder.WinderEngine;
import org.ebayopensource.winder.WinderException;
import org.quartz.Scheduler;
import org.quartz.impl.DefaultThreadExecutor;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.plugins.history.LoggingJobHistoryPlugin;
import org.quartz.plugins.history.LoggingTriggerHistoryPlugin;
import org.quartz.simpl.SimpleInstanceIdGenerator;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.spi.ThreadPool;
import org.quartz.utils.DBConnectionManager;
import org.quartz.utils.PoolingConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Quartz Initializer
 *
 * @author Sheldon Shao xshao@ebay.com on 10/20/16.
 * @version 1.0
 */
public class QuartzInitializer {


    private static Logger log = LoggerFactory.getLogger(QuartzInitializer.class);

    public Scheduler init(WinderEngine engine) {
        WinderConfiguration configuration = engine.getConfiguration();
        DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();
        int numThreads = configuration.getInt("winder.quartz.numThreads", 50);


        String quartzType = configuration.getString("winder.quartz.scheduler_type");
        String dsName = configuration.getString("winder.quartz.dsname");

        Scheduler scheduler = null;
        try {
            if ("IN_MEMORY_SCHEDULER".equals(quartzType) || (quartzType == null && dsName == null)) {
                factory.createVolatileScheduler(numThreads);
                scheduler = factory.getScheduler();

                if (log.isInfoEnabled()) {
                    log.info("Scheduler manager starting IN_MEMORY_SCHEDULER");
                }
            } else {
                ThreadPool threadPool = new SimpleThreadPool(numThreads, Thread.NORM_PRIORITY);
                threadPool.initialize();
                String instanceId = (new SimpleInstanceIdGenerator()).generateInstanceId();

                if (dsName == null) {
                    dsName = "winder_quartz";
                }
                String jdbcDriver = configuration.getString("winder.quartz.ds.driver");
                String jdbcUrl = configuration.getString("winder.quartz.ds.url");
                String jdbcUser = configuration.getString("winder.quartz.ds.username");
                String jdbcPassword = configuration.getString("winder.quartz.ds.password");
                int poolSize = configuration.getInt("winder.quartz.ds.pool_size", numThreads + 15);

                String validate = configuration.getString("winder.quartz.ds.validate_sql", "SELECT 1 /* ping */");
                PoolingConnectionProvider pooling = new PoolingConnectionProvider(
                        jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword, poolSize, validate);
                DBConnectionManager dbMgr = DBConnectionManager.getInstance();
                dbMgr.addConnectionProvider(dsName, pooling);

                boolean enableQuartz = configuration.getBoolean("winder.quartz.enable", true);

                if (enableQuartz) {
                    String tablePrefix = configuration.getString("winder.quartz.ds.table_prefix", "WINDER_");
                    int checkInterval = configuration.getInt("winder.quartz.checkin_interval", 7500);
                    String clusterName = engine.getClusterName();
                    JobStoreTX jdbcJobStore = new WinderJobStoreTx();
                    jdbcJobStore.setDataSource(dsName);
                    jdbcJobStore.setTablePrefix(tablePrefix);
                    jdbcJobStore.setIsClustered(true);
                    jdbcJobStore.setClusterCheckinInterval(checkInterval);

                    String hostName;
                    try {
                        InetAddress inet = InetAddress.getLocalHost();
                        hostName = inet.getHostName();
                    } catch (UnknownHostException e) {
                        hostName = "unknownHost";
                    }
                    jdbcJobStore.setInstanceId(hostName);
                    jdbcJobStore.setDriverDelegateClass("org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
                    jdbcJobStore.setThreadPoolSize(poolSize);

                    // To fix the quartz misfire issue
                    DefaultThreadExecutor executor = new DefaultThreadExecutor();
                    long idleWaitTime = configuration.getLong("winder.quartz.idle_wait_time", 30000L);
                    long dbFailureRetryInterval = configuration.getLong("winder.quartz.db_failure_retry_interval",
                            10000L);
                    long batchTimeWindow = configuration.getLong("winder.quartz.batch_time_window", 1000L);

                    boolean enableQuartzPlugins = configuration.getBoolean("winder.quartz.plugins.enable", false);
                    if (enableQuartzPlugins) {
                        Map<String, SchedulerPlugin> schedulerPluginMap = new HashMap<String, SchedulerPlugin>();
                        schedulerPluginMap.put("LoggingTriggerHistoryPlugin", new LoggingTriggerHistoryPlugin());
                        schedulerPluginMap.put("LoggingJobHistoryPlugin", new LoggingJobHistoryPlugin());

                        factory.createScheduler(clusterName, instanceId, threadPool, executor, jdbcJobStore,
                                schedulerPluginMap, null, 0, idleWaitTime, dbFailureRetryInterval, false, null,
                                numThreads, batchTimeWindow);
                    } else {
                        factory.createScheduler(clusterName, instanceId, threadPool, executor, jdbcJobStore, null, null,
                                0, idleWaitTime, dbFailureRetryInterval, false, null, numThreads, batchTimeWindow);
                    }
                    scheduler = factory.getScheduler(clusterName);
                    if (log.isInfoEnabled()) {
                        log.info("Scheduler manager starting with:" + jdbcUrl);
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Scheduler manager disabled!");
                    }
                }
            }

            return scheduler;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failure initializing quartz", e);
            }
            throw new IllegalStateException("Unable to initialize quartz", e);
        }
    }

}
