package org.jumpmind.pos.util.clientcontext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class ClientContentExtractionFilter extends OncePerRequestFilter {
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ClientContext clientContext;

    @Value("${openpos.installationId:'not set'}")
    String installationId;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Enumeration<String> headerNames = request.getHeaderNames();
        clientContext.put("deviceId", installationId);
        while( headerNames.hasMoreElements() ) {
            String header = headerNames.nextElement();
            if(header.startsWith("ClientContext-")){
                //Get the name after the prepended ClientContext- and put the value in the client context map
                String contextName = header.substring(header.indexOf('-') + 1);
                clientContext.put(contextName, request.getHeader(header));
            }
        }
        filterChain.doFilter(request, response);
    }
}
