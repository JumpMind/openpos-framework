package org.jumpmind.pos.translate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

/**
 * TranslatorService looks for all {@literal @}Translator annotated beans found 
 * hierarchically on paths defined by {@literal @}TranslatorScan annotations and
 * automatically adds them into the Spring application context.  Translator beans
 * should then be obtained by calling the {@link #getTranslator(String, ILegacyScreen, String)}
 * method.
 *
 */
@Service
public class TranslatorService {
    private static final Logger logger = LoggerFactory.getLogger(TranslatorService.class);

    @Autowired
    protected ApplicationContext appContext;
    
    /**
     * Fetches a translator bean from the Spring application context.
     * @param id The identifier of the translator as specified in the {@literal @}Translator
     *  annotation for the bean.
     * @param screen The legacy screen instance to pass to the translator when created/fetched
     *  from the spring context.
     * @param appId Optional parameter to specify the appId of the translator being
     *  sought. The {@literal @}Translator annotation allows one to specify which
     *  appIds a translator is applicable for. 
     * @return The translator associated with the given id and optional appId or {@code null}
     *  if the translator could not be obtained.
     */
    public ITranslator getTranslator(String id, ILegacyScreen screen, String appId) {
        ITranslator translator = (ITranslator) this.appContext.getBean(id, screen);
        if (translator != null) {
            // If the Translator has specified one or more appIds, make sure the given
            // appId is in the list.  Otherwise, the translator isn't configured
            // to be used with this app.
            String[] translatorAppIds = translator.getClass().getAnnotation(Translator.class).appIds();
            if (translatorAppIds == null || translatorAppIds.length == 0) {
                // There are no specific appIds configured for this translator, so
                // it doesn't matter what specific appId this might be for
                return translator;
            } else {
                if (appId == null || appId.trim().length() == 0) { // No specific appId given 
                    throw new RuntimeException(
                        String.format("The translator class '%s' is only " + 
                            "configured for the following appIds: %s. " +
                            "Check the @Translator annotation for the translator.", 
                            translator.getClass().getName(), StringUtils.join(translatorAppIds, ", ")
                        )
                    );
                } else {
                    boolean found = Arrays.stream(translatorAppIds).anyMatch(a -> a.equalsIgnoreCase(appId));
                    if (found) {
                        return translator;
                    } else {
                        throw new RuntimeException(
                                String.format("The translator class '%s' is only " + 
                                    "configured for the following appIds: %s. " +
                                    "Check the @Translator annotation for the translator.", 
                                    translator.getClass().getName(), StringUtils.join(translatorAppIds, ", ")
                                )
                            );
                    }
                }
            }
        } else {
            logger.error("Translator not found with id '{}'", id);
            return translator;
        }
    }
    
    @EventListener
    protected void onApplicationEvent(ContextRefreshedEvent event) {
        // Locate all beans in the App context with the @TranslatorScan annotation
        Collection<Object> beans = appContext.getBeansWithAnnotation(TranslatorScan.class).values();
        
        Set<String> scanPackages = beans.stream()
            .flatMap(b -> { 
                String[] pkgs = b.getClass().getAnnotation(TranslatorScan.class).value();
                if (pkgs != null) {
                    return Arrays.stream(pkgs);
                } else {
                    logger.warn("Failed to read value of TranslatorScan annotated class '{}'", b.getClass().getName());
                    return Arrays.stream(new String[] {});
                }
            })
            .collect(Collectors.toSet());

        logger.debug("Packages to scan for translators: {}", scanPackages);
        scanPackages.forEach(p -> this.registerTranslatorBeans(p));
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends ITranslator> Map<String, Class<T>> registerTranslatorBeans(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = createScanningComponentProvider();
        Map<String, Class<T>> translatorClassesById = new HashMap<>();
        provider.findCandidateComponents(basePackage).forEach(beanDef -> {
            try {
                Class<T> translatorClass = (Class<T>) Class.forName(beanDef.getBeanClassName());
                Translator translatorAnnotation = translatorClass.getAnnotation(Translator.class);
                String[] translatorIds = translatorAnnotation.value();
                for (String translatorId : translatorIds) {
                    
                    if (translatorClassesById.containsKey(translatorId)) {
                        logger.error("{} is using an @Translator value of '{}', which is already used by {}.", 
                           translatorClass.getName(), translatorId, translatorClassesById.get(translatorId).getName());
                    } else {
                        if (ITranslator.class.isAssignableFrom(translatorClass)) {
                            translatorClassesById.put(translatorId, (Class<T>)translatorClass);
                            if (appContext != null 
                                && appContext.getAutowireCapableBeanFactory() instanceof BeanDefinitionRegistry) {
                                if (! appContext.containsBean(translatorId)) {
                                    this.registerTranslatorInSpring(translatorClass, (BeanDefinitionRegistry) appContext.getAutowireCapableBeanFactory());
                                    logger.debug("Successfully registered translator {} in the Spring app context", translatorClass.getName());
                                } else {
                                    logger.warn("Skipped duplicate attempt to register translator {} in the Spring context using id '{}'", translatorClass.getName(), translatorId);
                                }
                            } else {
                                logger.error("Can't register translator  {} in the Spring app context since the app context isn't of the expected type", translatorClass.getName());
                            }
                        } else {
                            logger.error("Expected {} with @Translator value of '{}' to be an instance of ITranslator", translatorClass.getName(), translatorId);
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("Failed to process @Translator annotation for {}", beanDef.getBeanClassName());
            }
        });
        
        return translatorClassesById;
    }
    
    protected <T extends ITranslator> void registerTranslatorInSpring(Class<T> translatorClass, BeanDefinitionRegistry registry) {
        Translator translatorAnnotation = translatorClass.getAnnotation(Translator.class);
        String[] translatorIds = translatorAnnotation.value();
        
        for (String translatorId : translatorIds) {
            BeanDefinition beanDef = new GenericBeanDefinition();
            beanDef.setBeanClassName(translatorClass.getName());
            beanDef.setScope(translatorAnnotation.scopeName());
            registry.registerBeanDefinition(translatorId, beanDef);
        }
    }

    protected ClassPathScanningCandidateComponentProvider createScanningComponentProvider() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Translator.class));
        return provider;
    }
}
