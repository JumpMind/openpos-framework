package org.jumpmind.pos.util;

import java.io.ByteArrayInputStream;

public class MutableByteArrayInputStream extends ByteArrayInputStream {

    public MutableByteArrayInputStream() {
        super (new byte[0]);
    }

    public synchronized void setBuffer(byte[] buffer) {
        this.buf = buffer;
        this.pos = 0;
        this.count = buffer.length;
    }

}
