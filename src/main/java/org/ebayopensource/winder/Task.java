package org.ebayopensource.winder;

/**
 * New Interface for Task
 *
 * @author Sheldon Shao xshao@ebay.com on 10/12/16.
 * @version 1.0
 */
public interface Task<TI extends TaskInput, TR extends TaskResult> {

    TaskState execute(TaskContext<TI, TR> ctx, TI input, TR result) throws Exception;
}