package org.jumpmind.pos.print;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jumpmind.pos.util.AppUtils;

import java.io.InputStream;
import java.io.OutputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PeripheralConnection {

    private long RESET_TIMEOUT = 10000;

    OutputStream out;  // write to printer.
    InputStream in;  // read status, etc. from printer.
    Object rawConnection;

    public void resetInput() {
        if (in != null) {
            try {
                long start = System.currentTimeMillis();
                while (in.available() > 0 && (System.currentTimeMillis()-start) <= RESET_TIMEOUT) {
                    int readByte = in.read();
                    log.debug("Flushing read byte {}", readByte);
                }
            } catch (Exception ex) {
                throw new PrintException("Failed to resetInput", ex);
            }
        }
    }

    public void close() {
        IOUtils.closeQuietly(in, out);
        AppUtils.sleep(300); // Toshiba TCX seems to need a pause when closing and reopening a socket quickly.
    }

}
