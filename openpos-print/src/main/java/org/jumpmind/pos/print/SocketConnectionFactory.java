package org.jumpmind.pos.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class SocketConnectionFactory implements IConnectionFactory {

    static final Logger log = LoggerFactory.getLogger(SocketConnectionFactory.class);

    OutputStream os;

    @Override
    public OutputStream open(Map<String, Object> settings) {
        if (os == null) {
            String hostname = (String) settings.get("hostName");
            Integer port = (Integer) settings.get("port");
            try {
                log.info("Connecting to printer at {}:{}", hostname, port);
                Socket socket = new Socket(hostname, port);
                os = socket.getOutputStream();
                log.info("Connected to printer at {}:{}", hostname, port);
            } catch (Exception ex) {
                throw new PrintException(String.format("Failed to connect to printer at %s:%s",
                        hostname, port), ex);
            }
        }
        return os;
    }

    @Override
    public void close() {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }
}
