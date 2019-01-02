package org.jumpmind.pos.core.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping(value = "fileupload")
public class FileUploadService implements IFileUploadService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<String, BiConsumer<String,InputStream>> nodeUploadHandlers = new HashMap<>();
    
    public void registerNodeUploadHandler(String nodeId, String context, BiConsumer<String,InputStream> handler) {
        this.nodeUploadHandlers.put(this.makeNodeUploadHandlerKey(nodeId, context), handler);
        logger.info("Node file upload handler successfully registered for node '{}' at context '{}'", nodeId, context);
    }
    
    @Override
    public void registerNodeUploadHandler(IFileUploadHandler handler) {
        this.registerNodeUploadHandler(handler.getNodeId(), handler.getUploadContext(), handler.getUploadHandler());
    }    
    
    @RequestMapping(method = RequestMethod.GET, value = "ping")
    @ResponseBody
    public Pong ping() {
        logger.info("Recevied a ping request");
        return new Pong();
    }
    
    /**
     * Web service to receive a file upload from a client.
     * 
     * @param nodeId The node where the file should be copied to.
     * @param context The category/scope within the node that the uploaded file is associated with.  Allows for a way to handle
     *   files with differing IFileUploadHandlers. 
     * @param filename The name the file should have if/when saved to the local filesystem.
     * @param file The file data itself.
     * @throws IOException Thrown if there is any problem receiving/storing the file.
     */
    @RequestMapping(method = RequestMethod.POST, value = "uploadToNode")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({ 
        @ApiResponse(code = 500, message = "Error", response = Error.class),
        @ApiResponse(code = 404, message = "Context not found", response = Error.class)
    })
    public void uploadToNode(
        @RequestParam("nodeId") String nodeId, 
        @RequestParam("targetContext") String context,
        @RequestParam("filename") String filename,
        @RequestParam("file") MultipartFile file) throws IOException {
        
        String handlerKey = this.makeNodeUploadHandlerKey(nodeId, context);
        if (this.nodeUploadHandlers.containsKey(handlerKey)) {
            BufferedInputStream is = new BufferedInputStream(file.getInputStream());
            this.nodeUploadHandlers.get(handlerKey).accept(filename, is);
        } else {
            String err = String.format("No upload handler exists for %s, has one been registered?", this.makeNodeUploadHandlerKey(nodeId, context));
            logger.error(err);
            throw new ContextNotFoundException(err);
        }
    }
    
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected Error handleError(Throwable error) {
        return new Error(error.getMessage(), ExceptionUtils.getStackTrace(error));
    }

    @ExceptionHandler(ContextNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Error handleContextNotFoundException(ContextNotFoundException ex) {
        return new Error(ex.getMessage(), ExceptionUtils.getStackTrace(ex));
    }
    
    protected String makeNodeUploadHandlerKey(String nodeId, String context) {
        return String.format("%s/%s", nodeId, context);
    }
    
    class Pong implements Serializable {
        private static final long serialVersionUID = 1L;

        public boolean isPong() {
            return true;
        }
    }
    
    class ContextNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        
        public ContextNotFoundException() {
        }
        public ContextNotFoundException(String message) {
            super(message);
        }
        public ContextNotFoundException(String message, Throwable ex) {
            super(message, ex);
        }
    }
    
    
    @ApiModel(description = "The model an exception will be mapped to")
    class Error implements Serializable {
        private static final long serialVersionUID = 1L;

        private String message;
        private String stackTrace;

        public Error(String message) {
            this.message = message;
        }
        
        public Error(String message, String stackTrace) {
            this.message = message;
            this.stackTrace = stackTrace;
        }

        public Error() {
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }

        public String getStackTrace() {
            return stackTrace;
        }

    }

}
