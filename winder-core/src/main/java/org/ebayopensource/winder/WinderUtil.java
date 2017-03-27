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
package org.ebayopensource.winder;

import org.ebayopensource.winder.quartz.QuartzEngine;
import org.ebayopensource.winder.quartz.QuartzJobId;

/**
 * Winder Engine Access Point
 *
 * @author Sheldon Shao xshao@ebay.com on 3/26/17.
 * @version 1.0
 */
public class WinderUtil {

    /**
     * Winder Access Point
     *
     * @return Access Point
     */
    public static WinderEngine getEngine() {
        return QuartzEngine.getInstance();
    }

    public static boolean isValidJobId(String fromString, String clusterName) {
        if (fromString == null || fromString.length()==0) {
            return false;
        }
        String[] parts = new String[3];
        try {
            int i = fromString.indexOf(JobId.SEP);
            int j = fromString.indexOf(JobId.SEP,i+1);
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

    public static JobId toJobId(String fromString) {
        if (fromString == null || fromString.length()==0) {
            throw new IllegalArgumentException("Job string is null or empty");
        }
        String[] parts = new String[3];
        try {
            int i = fromString.indexOf(JobId.SEP);
            int j = fromString.indexOf(JobId.SEP,i+1);
            if (j<0) {
                j = fromString.length();
            }
            parts[0] = fromString.substring(0,i);
            parts[1] = fromString.substring(i+1,j);
            if (j<fromString.length()) {
                parts[2] = fromString.substring(j+1);
            } else {
                // by default, add one
                parts[2] = getEngine().getClusterName();
            }
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse jobId: " + fromString, e);
        }

        return new QuartzJobId(parts[0], parts[1], parts[2]);
    }
}
