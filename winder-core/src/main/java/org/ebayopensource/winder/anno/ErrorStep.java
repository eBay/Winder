package org.ebayopensource.winder.anno;

import org.ebayopensource.winder.StatusEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate the step is an final error handling.
 * It will try to get the ERROR step by name "ERROR" first,
 *    if there is no error step specified.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorStep {

    StatusEnum value() default StatusEnum.ERROR;
}
