package org.jumpmind.pos.persist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Augmented {
    String name() default "";
    String indexPrefix() default "";
    String[] names() default {};
}
