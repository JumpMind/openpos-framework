package org.jumpmind.pos.print;

import java.io.OutputStream;
import java.util.Map;

public interface IConnectionFactory {

    public PrinterConnection open(Map<String, Object> settings);
    public void close();

}
