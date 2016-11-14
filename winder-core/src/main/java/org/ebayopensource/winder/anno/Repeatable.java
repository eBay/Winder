package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate the step is non repeatable.
 * This annotation is to avoid infinite loop.
 * If these steps annotated with this annotation and repeatable is false,
 * Winder will check that, if the next step is same as last.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeatable {
    /**
     * Whether the step is repeatable or not
     *
     * @return
     */
    boolean value() default true;
}
