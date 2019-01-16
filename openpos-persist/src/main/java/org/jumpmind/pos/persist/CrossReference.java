package org.jumpmind.pos.persist;


public @interface CrossReference {
    String field() default "";
    String refersToField() default "";
}
