package org.ebayopensource.winder.steps;


import org.apache.commons.lang3.StringUtils;
import org.ebayopensource.common.util.Parameters;
import org.ebayopensource.winder.Step;
import org.ebayopensource.winder.TaskContext;
import org.ebayopensource.winder.TaskInput;
import org.ebayopensource.winder.TaskResult;
import org.ebayopensource.winder.anno.Timeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.ebayopensource.winder.anno.Timeline.ERROR;
import static org.ebayopensource.winder.anno.Timeline.START;
import static org.ebayopensource.winder.anno.Timeline.STOP;

/**
 * Timeline Step
 *
 * Created by xshao on 6/7/16.
 */
public class TimelineStep<TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> extends StepWrapper<TI, TR, C> {

    //Fallback
    private final static String LAST_STAGE = "LAST_STAGE";

    private final static String LAST_STEP = "LAST_STEP";

    private final static String GROUPS = "GROUPS";
    private final static String GROUP = "GROUP_";
    private final static String GROUP_START = "GROUP_START";
    private final static String GROUP_END = "GROUP_END";
    private final static String LABEL = "LABEL";
    private final static String DESCRIPTION = "DESCRIPTION";

    public static final String TIMELINE = "Timeline";

    private Timeline timeline;

    private String currentStepName;

    //@Before("execution(@Timeline(state=START) * *.*(..)) && @annotation(timeline)")
    private boolean isBefore = false;

    /*
    @After(	"(execution(@Timeline(state=STOP) * *.*(..)) || " +
			"execution(@Timeline(stage=ERROR) * *.*(..)) || " +
			"execution(@Timeline(stage=CANCEL) * *.*(..))) && " +
			"@annotation(timeline)")
     */
    private boolean isAfter = false;


    //@Around("execution(@Timeline(state=AROUND) * *.*(..)) && @annotation(timeline)")
    private boolean isAround = false;

    private String state;

    private String timelineStep;

    private static Logger log = LoggerFactory.getLogger(TimelineStep.class);

    public TimelineStep(Step<TI, TR, C> step, Timeline timeline) {
        super(step);
        this.timeline = timeline;
        this.currentStepName = step.name();

        state = timeline.state();
        timelineStep = timeline.step();
        timelineStep = StringUtils.isEmpty(timelineStep) ? currentStepName : timelineStep;

        isBefore = "START".equals(state);
        isAfter = "STOP".equals(state) || "ERROR".equals(timelineStep) || "CANCEL".equals(timelineStep);
        isAround = "AROUND".equals(state);
    }

    @Override
    public void process(C context) throws Exception {
        if (isBefore || isAround) {
            if (log.isDebugEnabled()) {
                log.debug("Start Timeline Step: " + timelineStep);
            }
            insertTime(context, timelineStep,
                    isAround ? START : timeline.state(), timeline.groupEnabled(),
                    timeline.groupStart(), timeline.groupEnd(), timeline.label(), timeline.description());
        }

        super.process(context);

        if (isAfter || isAround) {
            if (log.isDebugEnabled()) {
                log.debug("End Timeline Step: " + timelineStep);
            }
            insertTime(context, timelineStep,
                    isAround ? STOP : timeline.state(), timeline.groupEnabled(),
                    timeline.groupStart(), timeline.groupEnd(), timeline.label(), timeline.description());
        }
    }

    private void insertTime(C jobContext, String step, String state,
                            boolean groupEnabled, boolean groupStart, boolean groupEnd,
                            String label, String description) {

        long time = System.currentTimeMillis() / 1000;

        Parameters<Object> taskResult = jobContext.getTaskResult();

        // Get the Timeline from jsonResult, if null creates new object
        Map<String, Object> timeline = getTimeline(taskResult);

        if (step.equalsIgnoreCase(ERROR)) {
            String currentStep = (String) timeline.get(LAST_STEP);
            if (currentStep == null) {
                currentStep = (String)timeline.get(LAST_STAGE);
            }

            if (currentStep != null) {
                if (currentStep.indexOf('|') >= 0) {
                    String[] currentStepArr = StringUtils.split(currentStep, '|');
                    timeline = setGroupChildObj(currentStepArr[2], state, timeline, time,
                             Integer.parseInt(currentStepArr[1]), groupStart, groupEnd, label, description);
                } else {
                    timeline = setChildObj(currentStep, state, timeline, time, label, description);
                }
            }
        } else {
            if (groupEnabled) {
                int groupId = jobContext.getGroupId();

                timeline.put(LAST_STEP, GROUPS + "|" + groupId + "|" + step);
                timeline = setGroupChildObj(step, state, timeline, time,
                        groupId, groupStart, groupEnd, label, description);
            } else {
                timeline.put(LAST_STEP, step);
                timeline = setChildObj(step, state, timeline, time, label, description);
            }
        }

        // Adding the Timeline back to the jsonResult
        taskResult.put(TIMELINE, timeline);
    }

    private Map<String, Object> getTimeline(Parameters<Object> jsonResult) {
        Map<String, Object> timeline = (Map<String, Object>) jsonResult.get(TIMELINE);
        if (timeline == null) {
            timeline = new HashMap<>();
        }
        return timeline;
    }

    private Map<String, Object> setChildObj(String step, String state,
                                            Map<String, Object> timeline, long time,
                                            String label, String description) {

        Map<String, Object> stateObj;

        if (timeline.containsKey(step)) {
            stateObj = (Map<String, Object>) timeline.get(step);
        } else {
            stateObj = new HashMap<>();
        }

        if (!stateObj.containsKey(state)
                || stateObj.containsKey(STOP)) {
            stateObj.put(state, time);
        }

        if (StringUtils.isNotBlank(label)) {
            stateObj.put(LABEL, label);
        }

        if (StringUtils.isNotBlank(description)) {
            stateObj.put(DESCRIPTION, description);
        }

        timeline.put(step, stateObj);
        return timeline;
    }

    private Map<String, Object> setGroupChildObj(String step, String state,
                                                 Map<String, Object> timeline, long time,
                                                 int groupId, boolean groupStart, boolean groupEnd,
                                                 String label, String description) {

        Map<String, Object> groups = new HashMap<>(4);
        Map<String, Object> groupStep = new HashMap<>(4);
        Map<String, Object> groupState = new HashMap<>(4);
        Map<String, Object> groupCount = new HashMap<>(4);

        if (timeline.containsKey(GROUPS)) {
            groups = (Map<String, Object>) timeline.get(GROUPS);

            if (groups.containsKey(GROUP + groupId)) {
                groupCount = (Map<String, Object>) groups.get(GROUP + groupId);
                if (groupCount.containsKey(step)) {
                    groupState = (Map<String, Object>) groupCount.get(step);
                }
            }

            if (groupState.isEmpty()) {
                groupState.put(state, time);

                if (StringUtils.isNotBlank(label)) {
                    groupState.put(LABEL, label);
                }

                if (StringUtils.isNotBlank(description)) {
                    groupState.put(DESCRIPTION, description);
                }

                if (groupEnd) {
                    groupState.put(GROUP_END, groupEnd);
                }

                if (groupStart) {
                    groupState.put(GROUP_START, groupStart);
                }

                groupCount.put(step, groupState);

                if (!groups.containsKey(GROUP + groupId)) {
                    groups.put(GROUP + groupId, groupCount);
                    timeline.put(GROUPS, groups);
                }

            } else if (!groupState.containsKey(state)
                    || groupState.containsKey(STOP)) {
                groupState.put(state, time);
            }
        } else {
            groupState.put(state, time);

            if (StringUtils.isNotBlank(label)) {
                groupState.put(LABEL, label);
            }

            if (groupEnd) {
                groupState.put(GROUP_END, groupEnd);
            }

            if (groupStart) {
                groupState.put(GROUP_START, groupStart);
            }

            if (StringUtils.isNotBlank(description)) {
                groupState.put(DESCRIPTION, description);
            }

            groupStep.put(step, groupState);
            groups.put(GROUP + groupId, groupStep);

            timeline.put(GROUPS, groups);
        }

        return timeline;
    }
}
