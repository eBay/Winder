package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Job Definition
 *
 * @author Sheldon Shao xshao@ebay.com on 10/19/16.
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Job {
    /**
     * Job type, if it is blank, use class simple name instead
     *
     * @return
     */
    String type() default "";

    /**
     * Fist step code.
     * The step with lowest code will be picked if no firstStep was specified
     *
     * @return first step code
     */
    String firstStep();

    /**
     * It will try to get the ERROR step by name "ERROR" first,
     *    if there is no error step specified.
     * @return
     */
    String errorStep() default "ERROR";


    /**
     *  Steps is considered as "DONE"
     *
     * @return
     */
    String[] doneSteps();
}
