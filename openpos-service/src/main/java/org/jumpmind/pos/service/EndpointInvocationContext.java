package org.jumpmind.pos.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndpointInvocationContext {

    private String clientVersion;
    private String endpointPath;
    private Object endpoint;
    private Object[] arguments;
    private Object result;

}
