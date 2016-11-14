package org.ebayopensource.winder.metadata;

import org.ebayopensource.winder.StatusEnum;
import org.ebayopensource.winder.Step;

/**
 * Winder Step Metadata implementation
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
public class WinderStepMetadata implements StepMetadata {

    private String name;
    private boolean first;
    private boolean error;
    private boolean done;
    private StatusEnum finalStatus = StatusEnum.UNKNOWN;
    private transient Step step;
    private int code;
    private boolean repeatable = true;

    public WinderStepMetadata(String name, Step step) {
        setStep(step);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public boolean isFirst() {
        return first;
    }

    @Override
    public boolean isError() {
        return error;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public StatusEnum getFinalStatus() {
        return finalStatus;
    }

    @Override
    public Step toStep() {
        return step;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setFinalStatus(StatusEnum finalStatus) {
        this.finalStatus = finalStatus;
    }

    public void setStep(Step step) {
        this.step = step;
        this.name = step.name();
        this.code = step.code();
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
}
