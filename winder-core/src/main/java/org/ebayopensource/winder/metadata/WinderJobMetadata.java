package org.ebayopensource.winder.metadata;

import org.ebayopensource.winder.Step;

import java.util.*;

/**
 * Job Metadata
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public class WinderJobMetadata implements JobMetadata {

    private String jobType;
    private Class<? extends Step> jobClass;
    private List<StepMetadata> steps;
    private StepMetadata firstStep;
    private StepMetadata errorStep;
    private List<StepMetadata> doneSteps;

    private transient Map<Integer, StepMetadata> stepMetadataMap;

    public WinderJobMetadata() {
    }

    public WinderJobMetadata(Class<? extends Step> jobClass) {
        setJobClass(jobClass);
        setJobType(jobClass.getSimpleName());
    }

    @Override
    public String getJobType() {
        return jobType;
    }

    @Override
    public List<StepMetadata> getSteps() {
        return steps == null ? Collections.<StepMetadata>emptyList() : steps;
    }

    @Override
    public StepMetadata getFirstStep() {
        return firstStep;
    }

    @Override
    public StepMetadata getErrorStep() {
        return errorStep;
    }

    @Override
    public List<StepMetadata> getDoneSteps() {
        return doneSteps == null ? Collections.<StepMetadata>emptyList() : doneSteps;
    }

    @Override
    public StepMetadata getStep(int code) {
        return stepMetadataMap.get(code);
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Class<? extends Step> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<? extends Step> jobClass) {
        this.jobClass = jobClass;
    }

    public void setSteps(List<StepMetadata> steps) {
        init(steps);
    }

    private void init(List<StepMetadata> list) {
        this.steps = Collections.unmodifiableList(list);
        Map<Integer, StepMetadata> stepMetadataMap = new HashMap<>();
        StepMetadata first = null;
        StepMetadata error = null;
        List<StepMetadata> doneSteps = new ArrayList<>(4);
        int minCode = Integer.MAX_VALUE;
        for(StepMetadata stepMetadata : list) {
            if (stepMetadata.isFirst()) {
                if (first == null) {
                    first = stepMetadata;
                }
                else {
                    throw new IllegalArgumentException("Can't have two first steps:" + jobClass
                            + ", first:" + first.getName() + ", second:" + stepMetadata.getName());
                }
            }
            if (stepMetadata.isError()) {
                if (error == null) {
                    error = stepMetadata;
                }
                else {
                    throw new IllegalArgumentException("Can't have two error steps:" + jobClass
                            + ", first:" + error.getName() + ", second:" + stepMetadata.getName());
                }
            }

            if (stepMetadata.isDone()) {
                doneSteps.add(stepMetadata);
            }

            if (stepMetadata.getCode() < minCode) {
                minCode = stepMetadata.getCode();
            }

            stepMetadataMap.put(stepMetadata.getCode(), stepMetadata);
        }

        if (first == null) {
            first = stepMetadataMap.get(minCode);
        }
        if (first != null) {
            stepMetadataMap.put(-1, first);
        }

        setErrorStep(error);
        this.firstStep = first;
        this.doneSteps = Collections.unmodifiableList(doneSteps);
        this.stepMetadataMap = stepMetadataMap;
    }

    protected void setErrorStep(StepMetadata errorStep) {
        this.errorStep = errorStep;
    }
}
