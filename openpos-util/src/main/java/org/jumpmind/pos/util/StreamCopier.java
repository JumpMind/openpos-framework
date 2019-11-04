package org.jumpmind.pos.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * A thread that will run as a daemon and copies the given InputStream to the 
 * given OutputStream until it is either stopped (via {@link #stopCopying()}) or the
 * end of the given InputStream is reached.  Callers can control whether or not
 * the given OutputStream is closed when stopped (or when the end of the InputStream is
 * reached) and callers can also optionally pass in Consumer to be invoked directly 
 * before the OutputStream is closed.  This provides an opportunity to write
 * to the OutputStream before it is closed.
 */
@Slf4j
public class StreamCopier extends Thread {
    public static final int BUF_SIZE = 32;
    BufferedInputStream is;
    OutputStream os;
    boolean stopped = false;
    boolean closeOutput = false;
    Consumer<OutputStream> beforeClose;
    String name;
    
    public StreamCopier(InputStream is, OutputStream outputToStream) {
        this.is = is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is, BUF_SIZE);
        this.os = outputToStream;
        setDaemon(true);
    }
    public StreamCopier(String name, InputStream is, OutputStream outputToStream) {
        this(is, outputToStream);
        this.name = name;
    }
    public StreamCopier(String name, InputStream is, OutputStream outputToStream, boolean closeOutputWhenStopped) {
        this(name, is, outputToStream);
        this.closeOutput = closeOutputWhenStopped;
    }
    public StreamCopier(String name, InputStream is, OutputStream outputToStream, boolean closeOutputWhenStopped, Consumer<OutputStream> beforeClose) {
        this(name, is, outputToStream, closeOutputWhenStopped);
        this.beforeClose = beforeClose;
    }

    public Consumer<OutputStream> getBeforeClose() {
        return beforeClose;
    }
    
    public void setBeforeClose(Consumer<OutputStream> beforeClose) {
        this.beforeClose = beforeClose;
    }
    
    public String getCopierName() {
        return name;
    }
    
    public void setCopierName(String name) {
        this.name = name;
    }
    
    public void stopCopying() {
        this.stopped = true;
    }

    protected void closeOutputStream() {
        try {
            if (beforeClose != null) {
                beforeClose.accept(this.os);
            }
            this.os.close();
        } catch (IOException ex) {
            log.warn("Failure closing stream", ex);
        }
    }
    
    @Override
    public void start() {
        log.info("Copying started for {}InputStream. Copying input to: {}", 
            StringUtils.isNotBlank(this.name) ? "'" + this.name + "' " : "", this.os);
        super.start();
    }
    
    @Override
    public void run() {
        try {
            byte[] buffer = new byte[BUF_SIZE];
            int len = -1;
            while (! stopped && (len = this.is.read(buffer)) != -1) {
                this.os.write(buffer, 0, len);
            }
            if (this.closeOutput) {
                this.closeOutputStream();
            }
        } catch (IOException e) {
            log.error("Copying failure", e);
        }
        log.info("Copying stopped for {}InputStream", StringUtils.isNotBlank(this.name) ? "'" + this.name + "' " : "");
    }
}
