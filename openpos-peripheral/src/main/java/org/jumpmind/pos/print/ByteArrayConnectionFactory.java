package org.jumpmind.pos.print;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ByteArrayConnectionFactory implements IConnectionFactory {

    @Override
    public PeripheralConnection open(Map<String, Object> settings) {
        PeripheralConnection peripheralConnection = new PeripheralConnection();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        peripheralConnection.setOut(out);
        return peripheralConnection;
    }

    @Override
    public void close(PeripheralConnection peripheralConnection) {
        if (peripheralConnection.getOut() instanceof ByteArrayOutputStream) {
            IOUtils.closeQuietly(peripheralConnection.getOut());
        }
    }
}
