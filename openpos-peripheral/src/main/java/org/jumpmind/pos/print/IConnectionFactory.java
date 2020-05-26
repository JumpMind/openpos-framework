package org.jumpmind.pos.print;

import java.util.Map;

public interface IConnectionFactory {

    public static final String PROPERTY_FILENAME = "filename";
    public static final String PROPERTY_PORTNAME = "portName";

    public PeripheralConnection open(Map<String, Object> settings);
    public void close(PeripheralConnection peripheralConnection);

}
