package org.jumpmind.pos.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ByteArrayConnectionFactory implements IConnectionFactory {

    ByteArrayOutputStream out;

    @Override
    public OutputStream open(Map<String, Object> settings) {
        this.out = new ByteArrayOutputStream();
        return this.out;
    }

    @Override
    public void close() {
        if (this.out != null) {
            try {
                this.out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.out = null;
        }
    }
}
