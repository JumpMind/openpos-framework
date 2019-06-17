package org.jumpmind.pos.core.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface In {
    public String name() default "";
    public boolean autoCreate() default false;
    public boolean required() default true;
    public ScopeType scope();
}
