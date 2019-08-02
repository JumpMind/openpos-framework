package org.jumpmind.pos.print;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//import org.jumpmind.pos.devices.DevicesUtils;
//import org.jumpmind.pos.devices.model.DeviceConfigModel;
//import org.jumpmind.pos.devices.model.DeviceConfigPropModel;
//import org.jumpmind.pos.devices.service.DeviceCache;

import jpos.config.JposConfigException;
import jpos.config.JposEntry;
import jpos.config.simple.AbstractRegPopulator;
import jpos.config.simple.SimpleEntry;
import jpos.util.JposEntryUtility;
import jpos.util.tracing.Tracer;
import jpos.util.tracing.TracerFactory;

public class TestJposRegPopulator extends AbstractRegPopulator {

    private Tracer tracer = TracerFactory.getInstance().createTracer("TestJposRegPopulator");

    public TestJposRegPopulator() {
        super(TestJposRegPopulator.class.getName());
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public void save(@SuppressWarnings("rawtypes") Enumeration entries) throws Exception {
    }

    @Override
    public void save(@SuppressWarnings("rawtypes") Enumeration entries, String fileName) throws Exception {
    }

    @Override
    public void load() {
        try {
                @SuppressWarnings("unchecked")
                Hashtable<String, JposEntry> entries = this.getJposEntries();

                {
                    JposEntry entry = new SimpleEntry();
                    entry.addProperty(JposEntry.LOGICAL_NAME_PROP_NAME, "EpsonPrinter");
                    entry.addProperty(JposEntry.SERVICE_CLASS_PROP_NAME, EscpPOSPrinter.class.getName());
                    entry.addProperty(JposEntry.SI_FACTORY_CLASS_PROP_NAME, EscpServiceInstanceFactory.class.getName());
                    entry.addProperty("PrinterCommandLocations", "esc_p.properties,epson.properties");


                    entry.add(entry.createProp("hostname", "192.168.42.197", String.class));


                    entries.put(entry.getLogicalName(), entry);
                }
            {
                JposEntry entry = new SimpleEntry();
                entry.addProperty(JposEntry.LOGICAL_NAME_PROP_NAME, "BrotherPrinter");
                entry.addProperty(JposEntry.SERVICE_CLASS_PROP_NAME, EscpPOSPrinter.class.getName());
                entry.addProperty(JposEntry.SI_FACTORY_CLASS_PROP_NAME, EscpServiceInstanceFactory.class.getName());
                entry.addProperty("PrinterCommandLocations", "esc_p.properties,brother.properties");


                entry.add(entry.createProp("hostname", "192.168.42.172", String.class));


                entries.put(entry.getLogicalName(), entry);
            }
        } catch (Exception e) {
            lastLoadException = e;
            tracer.print(e);
        }
    }

//    protected void extractPropAttr(JposEntry jposEntry, DeviceConfigPropModel prop) throws JposConfigException {
//        String propName = prop.getPropertyName();
//        String propValueString = prop.getPropertyValue();
//        String propTypeString = prop.getPropertyType();
//
//        if (isBlank(propTypeString)) {
//            propTypeString = "String";
//        }
//
//        Object propValue = null;
//        Class<?> propType = null;
//
//        try {
//            propType = Class.forName((propTypeString.startsWith("java.lang.") ? propTypeString : "java.lang." + propTypeString));
//
//            if (JposEntryUtility.isValidPropType(propType) == false) {
//                throw new JposConfigException("Invalid property type: " + propTypeString + " for property named: " + propName);
//            }
//
//            propValue = JposEntryUtility.parsePropValue(propValueString, propType);
//
//            if (JposEntryUtility.validatePropValue(propValue, propType) == false) {
//                throw new JposConfigException("Invalid property type: " + propTypeString + " for property named: " + propName);
//            }
//
//            jposEntry.add(jposEntry.createProp(propName, propValue, propType));
//        } catch (ClassNotFoundException cnfe) {
//            throw new JposConfigException("Invalid property type: " + propTypeString + " for property named: " + propName, cnfe);
//        }
//
//    }

    @Override
    public void load(String fileName) {
        this.load();
    }

    @Override
    public URL getEntriesURL() {

        return null;
    }

    @Override
    public String getName() {

        return null;
    }

}
