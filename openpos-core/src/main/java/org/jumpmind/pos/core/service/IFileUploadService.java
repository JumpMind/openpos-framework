package org.jumpmind.pos.core.service;

import java.util.function.Consumer;

public interface IFileUploadService {
    public void registerNodeUploadHandler(String nodeId, String context, Consumer<FileUploadInfo> handler);
    public void registerNodeUploadHandler(IFileUploadHandler handler);
}
