package org.jumpmind.pos.print;

import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Iterator;

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
        String hostname = getRequiredJposProperty(jposentry,"hostname");
        String port = getJposProperty(jposentry, "port", "9100");
//        String connectTimeout = jposentry.getProp("connectTimeout");
//        String readTimeout = jposentry.getProp("readTimeout");

        try {
            logger.info("Connecting to printer '{}' at {}:{}", jposentry.getLogicalName(), hostname, port);
            Socket socket = new Socket(hostname, Integer.parseInt(port));
            OutputStream out = socket.getOutputStream();
            printer.setOutputStream(out);
            logger.info("Connected to printer '{}' at {}:{}", jposentry.getLogicalName(), hostname, port);
        } catch (Exception ex) {
            throw new PrintException(String.format("Failed to connect to %s printer at %s:%s",
                    jposentry.getLogicalName(), hostname, port), ex);
        }

        PrinterCommands printerCommands = new PrinterCommands();

        String printerCommandLocations = getJposProperty(jposentry, PRINTER_COMMAND_LOCATIONS, "esc_p.properties");

        String[] locationsSplit = printerCommandLocations.split(",");

        for (String printerCommandLocation : locationsSplit) {
            printerCommands.load(Thread.currentThread().getContextClassLoader().getResource(printerCommandLocation.trim()));
        }

        printer.setPrinterCommands(printerCommands);

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
