package org.jumpmind.pos.translate;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * An annotation that can be added to a class in order to define one or more
 * package locations that should be searched for classes that are annotated with
 * {@literal @}Translator annotations and implement the {@link ITranslator} interface.
 * Those classes will be loaded into the Spring context so that instances
 * of translators can be retrieved from the Spring context.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface TranslatorScan {
    /**
     * One or more packages that should be scanned for classes that are annotated
     * with {@literal @}Translator . 
     * @return The identifiers 
     */
    String[] value();
}
