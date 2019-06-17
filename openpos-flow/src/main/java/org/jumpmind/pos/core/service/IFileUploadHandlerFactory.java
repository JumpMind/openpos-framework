package org.jumpmind.pos.core.service;

public interface IFileUploadHandlerFactory {
    IFileUploadHandler getFileUploadHandler(String context, String nodeId);
}
