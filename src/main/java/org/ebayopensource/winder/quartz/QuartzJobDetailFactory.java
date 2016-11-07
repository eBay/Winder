package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.*;
import org.ebayopensource.winder.util.Guid;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import java.util.Date;

import static org.ebayopensource.winder.quartz.QuartzWinderConstants.*;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.KEY_JOBOWNER;
import static org.ebayopensource.winder.quartz.QuartzWinderConstants.KEY_JOBSTAGE;

/**
 * Job Detail Factory
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
public class QuartzJobDetailFactory implements WinderJobDetailFactory {

    private WinderEngine engine;

    public QuartzJobDetailFactory(WinderEngine engine) {
        this.engine = engine;
    }

    private Guid guid = new Guid();

    @Override
    public WinderJobDetail createJobDetail(TaskInput input) {
        Class clazz = input.getJobClass();

        String jobType = input.getJobType();
        jobType = jobType == null ? clazz.getSimpleName() : jobType;

        String jobOwner = input.getJobOwner();

        Date createDate = new Date();
        String jobName = jobType + "." + guid.nextPaddedGUID();
        String groupName = engine.formatShortDate(createDate);

        JobDataMap map = new JobDataMap();
        String jobInput = input.toJson();

        String createDateStr = engine.formatDate(createDate);

        String firstStep = null;

        if (Step.class.isAssignableFrom(clazz)) {
            //Make sure it was registered
            engine.getStepRegistry().register(clazz);
            Step s = engine.getStepRegistry().getFirstStep(clazz);
            firstStep = String.valueOf(s != null ? s.code() : 0);
        }
        else if (Runnable.class.isAssignableFrom(clazz)) {
            firstStep = "run";
        }
        else {
            throw new IllegalStateException("Unsupported job type:" + clazz.getName());
        }

        map.put(KEY_JOBCREATEDATE, createDateStr);
        map.put(KEY_JOBINPUT, jobInput);
        map.put(KEY_JOBSTATUS, StatusEnum.SUBMITTED.toString());
        map.put(KEY_JOBCLASS, clazz.getName());
        map.put(KEY_JOBSTAGE, firstStep);
        map.put(KEY_JOBOWNER, jobOwner);

        // Create Job Details
        JobDetail jd = JobBuilder.newJob(QuartzJob.class).withIdentity(jobName, groupName).storeDurably(true) // durability:
                // keep
                // even
                // when
                // no
                // triggers
                // left
                .requestRecovery(true) // recover: recover in cluster after
                // failures
                .usingJobData(map).build();
        JobId jobId = new QuartzJobId(groupName, jobName, engine.getClusterName());
        return new QuartzJobDetail(engine, jobId, jd);
    }
}
