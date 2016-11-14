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
package org.ebayopensource.winder.metadata;

import org.ebayopensource.winder.StatusEnum;
import org.ebayopensource.winder.Step;
import org.ebayopensource.winder.TaskContext;
import org.ebayopensource.winder.anno.*;
import org.ebayopensource.winder.steps.LoggingStep;
import org.ebayopensource.winder.steps.TimelineStep;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stage registry
 *
 * Created by xshao on 6/7/16.
 */
public class WinderStepRegistry implements StepRegistry {

    private Map<Class<?>, JobMetadata> jobs = new ConcurrentHashMap<>();

    @Override
    public Step lookup(Class<? extends Step> clazz, final int code) {
        JobMetadata metadata = getMetadata(clazz);
        if (metadata == null) {
            throw new IllegalArgumentException("No such job:" + clazz);
        }
        return metadata.getStep(code).toStep();
    }

    @Override
    public JobMetadata register(Class<? extends Step> e) {
        JobMetadata metadata = jobs.get(e);
        if (metadata != null) {
            return metadata;
        }

        if (!Enum.class.isAssignableFrom(e)) {
            throw new IllegalArgumentException("The class should be enum");
        }
        Class<? extends Enum> clazz = (Class<? extends Enum>)e;
        EnumSet<?> set = EnumSet.allOf(clazz);

        WinderJobMetadata jobMetadata = new WinderJobMetadata(e);

        List<StepMetadata> list = new ArrayList<>();

        Step errorStep = null;

        for (Object step : set) {
            Step orig = (Step)step;
            String origName = orig.name();

            Class procClass = orig.getClass();

            Method[] methods = procClass.getMethods();
            Class contextClass;
            for(Method method :methods) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    if ("execute".equals(method.getName())) {
                        if (TaskContext.class.isAssignableFrom(paramTypes[0])) {

                            orig = addMoreSteps(orig);

                            Timeline timeline = method.getAnnotation(Timeline.class);
                            if (timeline != null) {
                                orig = timeline(orig, timeline);
                            }

                            contextClass  = paramTypes[0];

                            //DEBUG ONLY
                            if (LoggerFactory.getLogger(contextClass).isDebugEnabled()) {
                                orig = new LoggingStep(orig);
                            }
                        }
                        break;
                    }
                }
            }


            WinderStepMetadata stepMetadata = new WinderStepMetadata(origName, orig);
            try {
                Field field = e.getField(origName);
                boolean first = field.isAnnotationPresent(FirstStep.class);
                stepMetadata.setFirst(first);
                StatusEnum statusEnum = StatusEnum.UNKNOWN;
                ErrorStep error = field.getAnnotation(ErrorStep.class);
                if (error != null) {
                    stepMetadata.setError(error != null);
                    statusEnum = error.value();
                    stepMetadata.setRepeatable(false);
                }
                else {
                    DoneStep doneStep = field.getAnnotation(DoneStep.class);
                    if (doneStep != null) {
                        stepMetadata.setDone(true);
                        statusEnum = doneStep.value();
                        stepMetadata.setRepeatable(false);
                    }
                    else {
                        Repeatable repeatable = field.getAnnotation(Repeatable.class);
                        stepMetadata.setRepeatable(repeatable == null || repeatable.value());
                    }
                }
                stepMetadata.setFinalStatus(statusEnum);

            } catch (NoSuchFieldException e1) {
                throw new IllegalArgumentException("No such field:" + origName);
            }

            list.add(stepMetadata);

            if ("ERROR".equals(origName)) {
                errorStep = orig;
            }
        }

        jobMetadata.setSteps(list);

        Job jobDesc = clazz.getAnnotation(Job.class);
        if (jobDesc != null) {
            if (jobDesc.type().length() > 0) {
                jobMetadata.setJobType(jobDesc.type());
            }
            if (jobDesc.group().length() > 0) {
                jobMetadata.setJobGroup(jobDesc.group());
            }
        }

        StepMetadata stepMetadata = jobMetadata.getErrorStep();
        if (stepMetadata == null && errorStep != null) {
            jobMetadata.setErrorStep(jobMetadata.getStep(errorStep.code()));
        }

        jobs.put(e, jobMetadata);
        return jobMetadata;
    }

    @Override
    public JobMetadata getMetadata(Class<? extends Step> clazz) {
        return jobs.get(clazz);
    }

    protected Step addMoreSteps(Step step) {
        return step;
    }

    protected Step timeline(Step step, Timeline timeline) {
        return new TimelineStep(step, timeline);
    }


//    /**
//     * Return step by name
//     *
//     * @param jobClass Enum Class
//     * @param stepName Name
//     * @return Step if it exists
//     */
//    private static Step getStepByName(Class<? extends Step> jobClass, String stepName) {
//        if (!StringUtils.isBlank(stepName)) {
//            Class<? extends Enum> castClass = (Class<? extends Enum>) jobClass;
//            return (Step) Enum.valueOf(castClass, stepName);
//        }
//        return null;
//    }
}
