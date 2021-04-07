package org.jumpmind.pos.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Endpoint {
    public static final String IMPLEMENTATION_DEFAULT     = "default";

    public static final String IMPLEMENTATION_PAY_ADYEN   = "adyen";
    public static final String IMPLEMENTATION_ANDROID     = "android";
    public static final String IMPLEMENTATION_APPRISS     = "appriss";
    public static final String IMPLEMENTATION_BLUE_FLETCH = "BlueFletch";
    public static final String IMPLEMENTATION_PAY_NCR     = "ncr";
    public static final String IMPLEMENTATION_SIMULATED   = "simulated";
    public static final String IMPLEMENTATION_TRAINING    = "training";
    public static final String IMPLEMENTATION_WINDOWS     = "windows";

    String path();

    String value() default "";
    
    String implementation() default IMPLEMENTATION_DEFAULT;

}