package org.jumpmind.pos.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModuleRegistry 
//implements BeanFactoryPostProcessor 
{

    final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    List<AbstractModule> modules;
    
    @PostConstruct
    public void loadModuleDatabaseDefaults () {
    	BufferedWriter out = null;
    	Date date = new Date();
    	
    	try {
            File file = new File(".h2.server.properties");
            out = new BufferedWriter(new FileWriter(file));
            out.write("#H2 Server Properties\n#" + DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(date) + "\n");
    	} catch (IOException e) {
    		log.warn("Unable to configure \".h2.server.properties\" file");
    	}
    	
    	int pos = 0;
    	if (out != null) {
    		for (AbstractModule module : modules) {
    			try {
    				out.write(pos + "=" + StringUtils.capitalize(module.getName()) + "|");
    				out.write(module.getDriver() + "|");
    				out.write(module.getURL() + "|\n");
    				pos++;
    			} catch (IOException e) {
    				log.warn("Unable to configure " + module.getName() + " in \".h2.server.properties\"");
    			}	
        	}
    	}
    	if (out != null) {
    		try {
    			out.close();
    		} catch (IOException e) {
    			
    		}
    	}
    }
    
    
    
//    @Autowired
//    private ConfigurableApplicationContext applicationContext;

//    private Map<String, ModuleDefinition> modules = new HashMap<>();

    // @EventListener(ContextRefreshedEvent.class)
//    @PostConstruct
//    public void initModules() {
//
//    }
    
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        for (String beanName : beanFactory.getBeanDefinitionNames()) {
//            Object bean = beanFactory.getBean(beanName);
//            if (bean != null) {
//                checkAndRegisterModule(bean);
//            }
//        }
//
//        for (ModuleDefinition module : modules.values()) {
//            init(module, beanFactory);
//        }        
//    }      
    
//    protected void init(ModuleDefinition module, ConfigurableListableBeanFactory beanFactory) {
//
//        List<Class<?>> tableClasses = 
//                getClassesForPackageAndAnnotation(module.getModulePackage(), Table.class);
//
//        Map<String, String> sessionContext = PeristTestUtil.getSessionContext();
//        
//        DBSessionFactory sessionFactory = beanFactory.getBean(DBSessionFactory.class);
//        sessionContext.put("module.tablePrefix", module.getModule().getTablePrefix());
//        
//        // init sessionFactory per this module. 
//        sessionFactory.init(
//                PeristTestUtil.getH2TestProperties(), 
//                sessionContext, 
//                tableClasses,
//                null); // TODO
//        module.getModule().start();
//    }
//
//    private void checkAndRegisterModule(Object bean) {
//        if (bean instanceof Module) {
//            Module module = (Module) bean;
//            ModuleDefinition moduleDefinition = new ModuleDefinition();
//            moduleDefinition.setModule(module);
//            moduleDefinition.setModulePackage(bean.getClass().getPackage().getName());
//            modules.put(module.getName(), moduleDefinition);
//        }
//    }
//
//    protected List<Class<?>> getClassesForPackageAndAnnotation(String packageName, Class<? extends Annotation> annotation) {
//        List<Class<?>> classes = new ArrayList<Class<?>>();
//        ClassPathScanningCandidateComponentProvider scanner =
//                new ClassPathScanningCandidateComponentProvider(true);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
//        for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
//            try {
//                final Class<?> clazz = Class.forName(bd.getBeanClassName());
//                classes.add(clazz);
//            } catch (ClassNotFoundException ex) {
//                log.error(ex.getMessage());
//            }
//        }    
//        return classes;
//    }

  
}
