package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Task Timer Annotation
 *
 * Created by xshao on 9/16/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskTimer {

    /**
     * Task type
     *
     * @return
     */
    String name();


    /**
     * Task description, if it is empty, use type
     * @return
     */
    String desc() default "";
}
