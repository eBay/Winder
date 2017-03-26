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

import org.ebayopensource.winder.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Quartz Job Detail Merger
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public class QuartzJobDetailMerger implements WinderJobDetailMerger {


    @Override
    public WinderJobDetail merge(WinderJobDetail dbDetail, WinderJobDetail runtimeDetail) {

        StatusEnum dbStatus = dbDetail.getStatus();
        //To preserve endDate
        Date endDate = dbDetail.getEndTime();
        if (endDate != null) {
            dbDetail.setEndTime(endDate);
        }

        StatusEnum runtimeStatus = runtimeDetail.getStatus();
        if (runtimeStatus != dbStatus && StatusEnum.SUBMITTED != dbStatus) {
            runtimeDetail.setStatus(dbStatus);
        }

        mergeChildJobIds(dbDetail, runtimeDetail);
        mergeActionsFromDbToRuntime(dbDetail, runtimeDetail);
        return doMoreMerge(dbDetail, runtimeDetail);
    }

    private void mergeChildJobIds(WinderJobDetail dbDetail, WinderJobDetail runtimeDetail) {
        JobId[] dbIds = dbDetail.getChildJobIds();
        if (dbIds != null && dbIds.length > 0) {
            JobId[] runtimeIds = runtimeDetail.getChildJobIds();
            int iterations = Math.max(dbIds.length, runtimeIds.length);
            List<JobId> result = new ArrayList<JobId>();
            JobId id;
            //The logic here takes into consideration that
            // 1. the new child id is always added to the end of the list
            // 2. the new child id(s) added to the db list will definitely be
            // be different from those to the runtime list
            for(int i = 0; i < iterations; i++) {
                id = null;
                if (i < dbIds.length) {
                    id = dbIds[i];
                    result.add(id);
                }
                if (i < runtimeIds.length) {
                    if (id == null || !runtimeIds[i].toString().equals(id.toString())) {
                        result.add(runtimeIds[i]);
                    }
                }
            }

            runtimeDetail.setChildJobIds(result.toArray(new JobId[result.size()]));
        }
    }



    private void mergeActionsFromDbToRuntime(WinderJobDetail dbDetail, WinderJobDetail runtimeDetail){
        runtimeDetail.setAwaitingForAction(dbDetail.isAwaitingForAction());

        List<UserAction> dbUserActions = dbDetail.getUserActions();
        List<UserAction> runtimeActions = runtimeDetail.getUserActions();

        int alertStatusSizeinDB = dbUserActions.size();
        int alertStatusSizeinContext = runtimeActions.size();
        if(alertStatusSizeinDB > alertStatusSizeinContext){
            for(int i= alertStatusSizeinContext; i< alertStatusSizeinDB; i++){
                runtimeDetail.addUserAction(dbUserActions.get(i));
            }
        }

        runtimeDetail.setAutoPause(dbDetail.isAutoPause());

//        JSONObject jobInputObjDB = (JSONObject) JobUtils.jsonFromText(dbMap.get(KEY_JOB_INPUT).toString());
//        JSONObject jobInputObjCtx = (JSONObject) JobUtils.jsonFromText(contextMap.get(KEY_JOB_INPUT).toString());
//
//        jobInputObjCtx.put(AUTO_PAUSE, jobInputObjDB.get(AUTO_PAUSE));
//        contextMap.put(KEY_JOB_INPUT, jobInputObjCtx.toString());
    }

    protected WinderJobDetail doMoreMerge(WinderJobDetail dbDetail, WinderJobDetail runtimeDetail) {
        return runtimeDetail;
    }
}
