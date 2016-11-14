package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate the step is the first step
 *
 * The step with lowest code will be picked if no firstStep was specified
 *
 * @author Sheldon Shao xshao@ebay.com on 11/13/16.
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstStep {
}