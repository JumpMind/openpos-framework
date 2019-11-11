package org.jumpmind.pos.core.service;

import java.io.StringWriter;

import org.apache.log4j.WriterAppender;

public class TestingBufferAppender extends WriterAppender {

    StringWriter buffer = new StringWriter();
    
    public TestingBufferAppender() {
        this.setWriter(buffer);
    }
}
