package org.jumpmind.pos.peripheral.poledisplay;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.print.IConnectionFactory;
import org.jumpmind.pos.print.PeripheralConnection;
import org.jumpmind.pos.print.PrintException;
import org.jumpmind.pos.util.ClassUtils;

import java.util.Map;

@Slf4j
public class PoleDisplay {

    final static byte CLEAR_DISPLAY = 0x0C; // NCR

    Map<String, Object> settings;
    PeripheralConnection peripheralConnection;
    IConnectionFactory connectionFactory;

    public PoleDisplay() {

    }

    public void open(Map<String,Object> settings) {
        this.settings = settings;
        try {
            this.connectionFactory =
                    (IConnectionFactory) ClassUtils.loadClass((String) this.settings.get("connectionClass")).newInstance();
        } catch (Exception ex) {
            throw new PrintException("Failed to create the connection factory for " + getClass().getName(), ex);
        }
        this.peripheralConnection = connectionFactory.open(this.settings);
    }

    public void close() {
        this.connectionFactory.close(peripheralConnection);
    }

    public void showText(String text) {
        try {
            peripheralConnection.getOut().write(CLEAR_DISPLAY);
            peripheralConnection.getOut().write(text.getBytes());
        } catch (Exception ex) {
            log.warn("Failed to write text to the PoleDisplay \"" + text + "\"", ex);
        }
    }
}
