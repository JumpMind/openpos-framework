package org.jumpmind.pos.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Map;

public class SocketConnectionFactory implements IConnectionFactory {

    static final Logger log = LoggerFactory.getLogger(SocketConnectionFactory.class);

    PrinterConnection printerConnection;

    @Override
    public PrinterConnection open(Map<String, Object> settings) {
        if (printerConnection == null) {
            String hostname = (String) settings.get("hostName");
            Object portObject = settings.get("port");
            int port = getInt(settings.get("port"), 9100);
            int soTimeout = getInt(settings.get("soTimeout"), 2500);
            try {
                log.info("Connecting to printer at {}:{}", hostname, port);
                Socket socket = new Socket(hostname, port);
                socket.setSoTimeout(soTimeout);
                printerConnection.setOut(socket.getOutputStream());
                printerConnection.setIn(socket.getInputStream());
                log.info("Connected to printer at {}:{}", hostname, port);
            } catch (Exception ex) {
                throw new PrintException(String.format("Failed to connect to printer at %s:%s",
                        hostname, port), ex);
            }
        }
        return printerConnection;
    }

    private int getInt(Object object, int defaultValue) {
        int value = defaultValue;
        if (object instanceof String) {
            value = Integer.parseInt((String)object);
        } else if (object instanceof Integer) {
            value = (Integer)object;
        }
        return value;
    }

    @Override
    public void close() {
        printerConnection.close();
    }
}
