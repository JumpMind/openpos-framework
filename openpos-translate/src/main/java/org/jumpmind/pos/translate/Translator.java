package org.jumpmind.pos.translate;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Classes annotated {@literal @}Translator are classes that implement the {@link ITranslator}
 * interface and can translate screen information from the legacy POS into
 * an OpenPOS {@code UIMessage}. A Spring BeanDefinition will be created for the
 * translator class and instances of the translator will be available from the
 * Spring application context as long as {@link TranslatorService#registerTranslatorBeans(String, org.springframework.context.ApplicationContext)
 * is invoked early in the application startup.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Translator {
    /**
     * One or more identifiers (such as a legacy screen id) that will be used for 
     * looking up an instance of the {@literal @}Translator annotated class in the Spring 
     * application context. 
     * @return The identifiers 
     */
    String[] value();
    
    /**
     * One or more OpenPOS application IDs (appId) that this translator is associated
     * with.  For example: <code>{"pos", "pricechecker"}</code>.  If no value is
     * is specified, then the translator will be available to all app IDs.
     * @return The application IDs
     */
    String[] appIds() default {};
    
    /**
     * One of the spring {@literal @}Scope values which controls if the Translator
     * is a singleton or prototype, for example.  Default scope is {@code prototype}
     * which will cause a new instance of the Translator to be created each time
     * it is retrieved from the Spring application context.
     */
    String scopeName() default "prototype";
}
