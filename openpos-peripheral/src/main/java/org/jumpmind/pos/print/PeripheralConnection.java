package org.jumpmind.pos.print;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeripheralConnection {

    OutputStream out;  // write to printer.
    InputStream in;  // read status, etc. from printer.
    Object rawConnection;

    public void close() {
        IOUtils.closeQuietly(in, out);
    }

}
