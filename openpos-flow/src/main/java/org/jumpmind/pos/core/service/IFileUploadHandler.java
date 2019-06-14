package org.jumpmind.pos.core.service;

import java.io.File;
import java.util.function.Consumer;

public interface IFileUploadHandler {
    
    Consumer<FileUploadInfo> getUploadHandler();
    String getUploadContext();
    String getNodeId();
    void setUploadDirectory(File file);
    File getUploadDirectory();
}
