package org.jumpmind.pos.print;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class FileConnectionFactory implements IConnectionFactory {

    @Override
    public PeripheralConnection open(Map<String, Object> settings) {

        String fileName = "";
        if (settings.containsKey(PROPERTY_FILENAME)) {
            fileName = (String)settings.get(PROPERTY_FILENAME);
        } else {
            fileName = (String)settings.get(PROPERTY_PORTNAME);
        }

        if (StringUtils.isEmpty(fileName)) {
            throw new PrintException("No filename could be determined for this connection factory. A " +
                    PROPERTY_FILENAME + " or " + PROPERTY_PORTNAME + " property is required.");
        }

        PeripheralConnection peripheralConnection = new PeripheralConnection();
        try {
            File file = new File(fileName);
            peripheralConnection.setOut(new FileOutputStream(file));
            peripheralConnection.setIn(new FileInputStream(file));
            return peripheralConnection;
        } catch (Exception ex) {
            throw new PrintException("Failed to open file named " + fileName, ex);
        }

    }

    @Override
    public void close(PeripheralConnection peripheralConnection) {
        if (peripheralConnection.getOut() instanceof FileOutputStream) {
            IOUtils.closeQuietly(peripheralConnection.getIn());
            IOUtils.closeQuietly(peripheralConnection.getOut());
        }
    }
}
