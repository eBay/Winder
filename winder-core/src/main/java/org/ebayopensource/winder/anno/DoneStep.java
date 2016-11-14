package org.ebayopensource.winder.anno;

import org.ebayopensource.winder.StatusEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the step is a done, that means after this step, the job will be quit.
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoneStep {
    StatusEnum value() default StatusEnum.COMPLETED;
}
