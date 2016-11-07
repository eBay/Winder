package org.ebayopensource.winder;

import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.winder.anno.Job;
import org.ebayopensource.winder.anno.Timeline;
import org.ebayopensource.winder.steps.LoggingStep;
import org.ebayopensource.winder.steps.TimelineStep;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Stage registry
 *
 * Created by xshao on 6/7/16.
 */
public class WinderStepRegistry implements StepRegistry {

    private Map<Class<?>, Map<Integer, ? extends Step>> steps = new HashMap<>();

    private Map<Class<?>, StepMeta> map = new HashMap<>();


    @Override
    public Step lookup(Class<? extends Step> clazz, final int code) {
        Map<Integer, ? extends Step> map = steps.get(clazz);
        return map.get(code);
    }

    private static class StepMeta {
        String type;
        Step firstStep;
        Step errorStep;
        Step[] doneSteps;


        private StepMeta(Class<? extends Step> jobClass,
                         String type,
                         String firstStepName,
                         String errorStepName, String[] doneStepNames,
                         Step minStep) {
            this.type = type != null ? type: jobClass.getSimpleName();
            firstStep = getStepByName(jobClass, firstStepName);
            if (firstStep == null) {
                firstStep = minStep;
            }
            errorStep = getStepByName(jobClass, errorStepName);

            if (doneStepNames != null && doneStepNames.length > 0) {
                doneSteps = new Step[doneStepNames.length];
                for(int i = 0; i < doneStepNames.length; i ++) {
                    doneSteps[i] = getStepByName(jobClass, doneStepNames[i]);
                }
            }
        }

    }

    @Override
    public void register(Class<? extends Step> e) {
        if (map.containsKey(e)) {
            return;
        }

        if (!Enum.class.isAssignableFrom(e)) {
            throw new IllegalArgumentException("The class should be enum");
        }
        Class<? extends Enum> clazz = (Class<? extends Enum>)e;
        EnumSet<?> set = EnumSet.allOf(clazz);


        HashMap<Integer, Step> steps = new HashMap<>();
        int minCode = Integer.MAX_VALUE;
        Step minStep = null;
        for (Object step : set) {
            Step orig = (Step)step;

            Class procClass = orig.getClass();
            Method[] methods = procClass.getMethods();
            Class contextClass;
            for(Method method :methods) {
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1) {
                    if ("process".equals(method.getName())) {
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


            steps.put(orig.code(), orig);
            if (orig.code() < minCode) {
                minCode = orig.code();
                minStep = orig;
            }
        }

        Job jobDesc = clazz.getAnnotation(Job.class);
        StepMeta meta = null;
        if (jobDesc != null) {
            meta = new StepMeta(e, jobDesc.type(), jobDesc.firstStep(), jobDesc.errorStep(), jobDesc.doneSteps(), minStep);
        }
        else {
            meta = new StepMeta(e, null, null, "ERROR", null, minStep);
        }
        this.map.put(e, meta);
        this.steps.put(e, steps);
    }

    @Override
    public String getJobType(Class<? extends Step> clazz) {
        return null;
    }

    protected Step addMoreSteps(Step step) {
        return step;
    }

    protected Step timeline(Step step, Timeline timeline) {
        return new TimelineStep(step, timeline);
    }

    @Override
    public Step getFirstStep(Class<? extends Step> clazz) {
        StepMeta steps = map.get(clazz);
        return steps != null ? steps.firstStep : null;
    }

    @Override
    public Step getErrorStep(Class<? extends Step> clazz) {
        StepMeta steps = map.get(clazz);
        return steps != null ? steps.errorStep : null;
    }

    @Override
    public Step[] getDoneSteps(Class<? extends Step> clazz) {
        StepMeta steps = map.get(clazz);
        return steps != null ? steps.doneSteps : new Step[0];
    }

    /**
     * Return step by name
     *
     * @param jobClass Enum Class
     * @param stepName Name
     * @return Step if it exists
     */
    private static Step getStepByName(Class<? extends Step> jobClass, String stepName) {
        if (!StringUtils.isBlank(stepName)) {
            Class<? extends Enum> castClass = (Class<? extends Enum>) jobClass;
            return (Step) Enum.valueOf(castClass, stepName);
        }
        return null;
    }
}
