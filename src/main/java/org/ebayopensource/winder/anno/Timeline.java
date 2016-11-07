package org.ebayopensource.winder.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Timeline {

    String step() default "";

    String state();

    boolean groupEnabled() default false;
    boolean groupStart() default false;
    boolean groupEnd() default false;

    String label() default "";
    String description() default "";

    String START = "START";
    String STOP = "STOP";
    String AROUND = "AROUND";
    String ERROR = "ERROR";


}
