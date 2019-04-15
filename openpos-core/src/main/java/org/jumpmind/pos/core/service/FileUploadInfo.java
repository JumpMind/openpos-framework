package org.jumpmind.pos.core.service;


import org.springframework.web.multipart.MultipartFile;

public class FileUploadInfo {
    protected String nodeId;
    protected String targetContext;
    protected String filename;
    protected MultipartFile file;
    protected Integer chunkSize;
    protected Integer chunkIndex;
    
    public FileUploadInfo() {
    }
    
    public FileUploadInfo(String nodeId, String targetContext, String filename, MultipartFile file) {
        this.nodeId = nodeId;
        this.targetContext = targetContext;
        this.filename = filename;
        this.file = file;
    }
    
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public String getTargetContext() {
        return targetContext;
    }
    public void setTargetContext(String targetContext) {
        this.targetContext = targetContext;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public MultipartFile getFile() {
        return file;
    }
    public void setFile(MultipartFile file) {
        this.file = file;
    }
    public Integer getChunkSize() {
        return chunkSize;
    }
    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }
    public Integer getChunkIndex() {
        return chunkIndex;
    }
    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
}
