/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
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
import org.quartz.JobKey;

public class QuartzJobId implements JobId {

    private final String m_group;
    private final String m_name;
    private final String m_cluster;
    private final JobKey m_key;

    private static final char SEP = '|';

    public QuartzJobId(JobKey key, String clusterName) {
        m_key = key;
        m_name = key.getName();
        m_group = key.getGroup();
        m_cluster = clusterName;
    }

    public QuartzJobId(String group, String name, String clusterName) {
        m_group = group;
        m_name = name;
        m_cluster = clusterName;
        m_key = new JobKey(m_name, m_group);
    }

    public static JobId createFromString(String fromString, String clusterName) {
        if (fromString == null || fromString.length()==0) {
            throw new IllegalArgumentException("Job string is null or empty");
        }
        String[] parts = new String[3];
        try {
            int i = fromString.indexOf(SEP);
            int j = fromString.indexOf(SEP,i+1);
            if (j<0) {
                j = fromString.length();
            }
            parts[0] = fromString.substring(0,i);
            parts[1] = fromString.substring(i+1,j);
            if (j<fromString.length()) {
                parts[2] = fromString.substring(j+1);
            } else {
                // by default, add one
                parts[2] = clusterName;
            }
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse jobId: " + fromString, e);
        }

        return new QuartzJobId(parts[0], parts[1], parts[2]);
    }

    public static boolean isValidJobId(String fromString, String clusterName) {
        if (fromString == null || fromString.length()==0) {
            return false;
        }
        String[] parts = new String[3];
        try {
            int i = fromString.indexOf(SEP);
            int j = fromString.indexOf(SEP,i+1);
            if (j<0) {
                j = fromString.length();
            }
            parts[0] = fromString.substring(0,i);
            parts[1] = fromString.substring(i+1,j);
            if (j<fromString.length()) {
                parts[2] = fromString.substring(j+1);
            } else {
                return false;
            }
            if (!clusterName.equals(parts[2])) {
                return false;
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    public JobKey getKey() {
        return m_key;
    }
    public String getName() {
        return m_name;
    }
    public String getGroup() {
        return m_group;
    }
    public String getCluster() {
        return m_cluster;
    }

    public String toString() {
        return m_group+SEP+m_name+SEP+m_cluster;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_group == null) ? 0 : m_group.hashCode());
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        QuartzJobId other = (QuartzJobId) obj;
        if (m_group == null) {
            if (other.m_group != null)
                return false;
        } else if (!m_group.equals(other.m_group))
            return false;
        if (m_name == null) {
            if (other.m_name != null)
                return false;
        } else if (!m_name.equals(other.m_name))
            return false;
        return true;
    }
}