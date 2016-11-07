package org.ebayopensource.winder;

/**
 * Step
 *
 * Created by xshao on 6/7/16.
 */
public interface Step<TI extends TaskInput, TR extends TaskResult, C extends TaskContext<TI, TR>> {

    String name();

    int code();

    void process(C context) throws Exception;
}
