package org.jumpmind.pos.print;

import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EscpServiceInstanceFactory implements JposServiceInstanceFactory {

    final String[] PROPS_TO_IGNORE = { "productURL", "serviceClass", "vendorName", "productDescription", "deviceCategory", "productName", "vendorURL", "logicalName", "jposVersion", "serviceInstanceFactoryClass", PRINTER_COMMAND_LOCATIONS };

    private final static Logger logger = LoggerFactory.getLogger(EscpServiceInstanceFactory.class);

    private final static String PRINTER_COMMAND_LOCATIONS = "PrinterCommandLocations";


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public JposServiceInstance createInstance(String s, JposEntry jposentry) throws JposException {
        if (!jposentry.hasPropertyWithName("serviceClass")) {
            throw new JposException(JposConst.JPOS_E_NOSERVICE, "The JposEntry does not contain the 'serviceClass' property");
        }
        try {
            String s1 = (String) jposentry.getPropertyValue("serviceClass");
            Class class1 = Class.forName(s1);
            Constructor constructor = class1.getConstructor(new Class[0]);
            JposServiceInstance instance = (JposServiceInstance) constructor.newInstance(new Object[0]);

            Iterator i = jposentry.getProps();

            while (i.hasNext()) {
                JposEntry.Prop prop = (JposEntry.Prop) i.next();

                if (!ignoreProperty(prop.getName())) {
                    try {
                        Field field = class1.getDeclaredField(prop.getName());
                        Class<?> type = field.getType();
                        Object value = prop.getValue();
                        if (type == int.class) {
                            value = new Integer(prop.getValueAsString());
                        }
                        field.setAccessible(true);
                        field.set(instance, value);
                    } catch (NoSuchFieldException e) {
                        logger.warn("No such property: " + prop.getName() + " exists on " + class1.getSimpleName());
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }

            if (instance instanceof IOpenposPrinter) {
                configureOpenposPrinter((IOpenposPrinter)instance, jposentry);
            }

            return instance;
        } catch (Exception ex) {
            throw new PrintException(String.format("Failed to create '%s' (%s)", s, ex.toString()), ex);
        }
    }

    private void configureOpenposPrinter(IOpenposPrinter printer, JposEntry jposentry) {
        String hostname = getJposProperty(jposentry,"hostname", "localhost");
        String port = getJposProperty(jposentry, "port", "9100");
        Map<String, Object> settings = new HashMap<>();
        settings.put("hostName", hostname);
        settings.put("port", port);
        settings.put("printerCommandLocations", getJposProperty(jposentry, PRINTER_COMMAND_LOCATIONS, "esc_p.properties"));
        settings.put("printWidth", getJposProperty(jposentry, "printWidth", "48"));
        settings.put("connectionClass", SocketConnectionFactory.class.getName());
        printer.init(settings);
    }

    protected String getRequiredJposProperty(JposEntry jposentry, String name) {
        String value = getJposProperty(jposentry, name, null);
        if (value == null) {
            throw new PrintException("Falied to get required jpos parameter '" + name + "'");
        }
        return value;
    }

    protected String getJposProperty(JposEntry jposentry, String name, String defaultValue) {
        if (jposentry.hasPropertyWithName(name)) {
            return jposentry.getPropertyValue(name).toString();
        } else {
            return defaultValue;
        }
    }

    protected boolean ignoreProperty(String name) {
        for (String string : PROPS_TO_IGNORE) {
            if (string.equals(name)) {
                return true;
            }
        }
        return false;
    }

}
