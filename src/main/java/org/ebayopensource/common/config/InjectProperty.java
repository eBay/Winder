package org.ebayopensource.common.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * InjectProperty Annotation
 *
 * Created by xshao on 10/1/16.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectProperty {

    /**
     * Property type
     *
     * @return
     */
    String name();
}
