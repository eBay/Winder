/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 * <p>
 * Licensed under the MIT license.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * <p>
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.quartz;

import org.ebayopensource.winder.JobId;
import org.ebayopensource.winder.WinderEngine;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.spi.ClassLoadHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Winder JDBC Delegate
 *
 * Winder has one more field than standard quartz, it categorizes job details.
 * So we have to store the jobCategory in db as well.
 *
 * @author Sheldon Shao xshao@ebay.com on 3/19/17.
 * @version 1.0
 */
public class WinderJDBCDelegate extends StdJDBCDelegate {


    private WinderEngine engine = QuartzEngine.getInstance();

    String COL_JOB_CATEGORY = "JOB_CATEGORY";

    /**
     * <p>
     * Select the JobDetail object for a given job name / group name.
     * </p>
     *
     * @param conn
     *          the DB Connection
     * @return the populated JobDetail object
     * @throws ClassNotFoundException
     *           if a class found during deserialization cannot be found or if
     *           the job class could not be found
     * @throws IOException
     *           if deserialization causes an error
     */
    public JobDetail selectJobDetail(Connection conn, JobKey jobKey,
                                     ClassLoadHelper loadHelper)
            throws ClassNotFoundException, IOException, SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(rtp(SELECT_JOB_DETAIL));
            ps.setString(1, jobKey.getName());
            ps.setString(2, jobKey.getGroup());
            rs = ps.executeQuery();

            JobDetailImpl job = null;

            QuartzJobDetail winderJobDetails = null;

            if (rs.next()) {
                job = new JobDetailImpl();

                String jobName = rs.getString(COL_JOB_NAME);
                String groupName = rs.getString(COL_JOB_GROUP);

                job.setName(jobName);
                job.setGroup(groupName);
                job.setDescription(rs.getString(COL_DESCRIPTION));
                job.setJobClass( loadHelper.loadClass(rs.getString(COL_JOB_CLASS), Job.class));
                job.setDurability(getBoolean(rs, COL_IS_DURABLE));
                job.setRequestsRecovery(getBoolean(rs, COL_REQUESTS_RECOVERY));

                JobId jobId = new QuartzJobId(groupName, jobName, engine.getClusterName());


                Map<?, ?> map = null;
                if (canUseProperties()) {
                    map = getMapFromProperties(rs);
                } else {
                    map = (Map<?, ?>) getObjectFromBlob(rs, COL_JOB_DATAMAP);
                }

                if (null != map) {
                    job.setJobDataMap(new JobDataMap(map));
                }

                winderJobDetails = new QuartzJobDetail(engine, jobId, job);
            }

            return winderJobDetails;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }

    /**
     * build Map from java.util.Properties encoding.
     */
    private Map<?, ?> getMapFromProperties(ResultSet rs)
            throws ClassNotFoundException, IOException, SQLException {
        Map<?, ?> map;
        InputStream is = (InputStream) getJobDataFromBlob(rs, COL_JOB_DATAMAP);
        if(is == null) {
            return null;
        }
        Properties properties = new Properties();
        try {
            properties.load(is);
        } finally {
            is.close();
        }
        map = convertFromProperty(properties);
        return map;
    }

    String WINDER_INSERT_JOB_DETAIL = "INSERT INTO "
            + TABLE_PREFIX_SUBST + TABLE_JOB_DETAILS + " ("
            + COL_SCHEDULER_NAME + ", " + COL_JOB_NAME
            + ", " + COL_JOB_GROUP + ", " + COL_DESCRIPTION + ", "
            + COL_JOB_CLASS + ", " + COL_IS_DURABLE + ", "
            + COL_IS_NONCONCURRENT +  ", " + COL_IS_UPDATE_DATA + ", "
            + COL_REQUESTS_RECOVERY + ", "
            + COL_JOB_DATAMAP + ", "
            + COL_JOB_CATEGORY + ") " + " VALUES(" + SCHED_NAME_SUBST + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * <p>
     * Insert the job detail record.
     * </p>
     *
     * @param conn
     *          the DB Connection
     * @param job
     *          the job to insert
     * @return number of rows inserted
     * @throws IOException
     *           if there were problems serializing the JobDataMap
     */
    public int insertJobDetail(Connection conn, JobDetail job)
            throws IOException, SQLException {
        ByteArrayOutputStream baos = serializeJobData(job.getJobDataMap());

        PreparedStatement ps = null;

        int insertResult = 0;

        try {
            ps = conn.prepareStatement(rtp(WINDER_INSERT_JOB_DETAIL));
            ps.setString(1, job.getKey().getName());
            ps.setString(2, job.getKey().getGroup());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getJobClass().getName());
            setBoolean(ps, 5, job.isDurable());
            setBoolean(ps, 6, job.isConcurrentExectionDisallowed());
            setBoolean(ps, 7, job.isPersistJobDataAfterExecution());
            setBoolean(ps, 8, job.requestsRecovery());
            setBytes(ps, 9, baos);

            String jobDetail = null;
            if (job instanceof QuartzJobDetail) {
                jobDetail = ((QuartzJobDetail)job).getJobCategory();
            }
            if (jobDetail == null) {
                jobDetail = "default";
            }
            ps.setString(10, jobDetail);

            insertResult = ps.executeUpdate();
        } finally {
            closeStatement(ps);
        }

        return insertResult;
    }
}
