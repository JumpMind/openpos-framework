package org.jumpmind.pos.devices;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.util.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class VirtualDeviceFilter implements Filter {

    RandomString session = new RandomString(9);

    @Value("${openpos.services.specificConfig.devices.implementation:not set}")
    String devicesImplementation;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String requestUri = ((HttpServletRequest) request).getRequestURI();
        if (requestUri.equals("/") && "virtual".equals(devicesImplementation)) {
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
                    (HttpServletResponse) response);
            chain.doFilter(request, wrappedResponse);
            String s = new String(wrappedResponse.getContentAsByteArray());
            final String BODY_TAG = "<body>";
            StringBuilder content = new StringBuilder();
            if(s != null && s.contains(BODY_TAG)) {
                content.append(s.substring(0, s.indexOf(BODY_TAG) - 1));
            }
            content.append("<body><script type=\"text/javascript\">\n");
            content.append("localStorage.setItem(\"serverName\", \"");
            content.append(request.getServerName());
            content.append("\");\n");
            content.append("localStorage.setItem(\"serverPort\", \"");
            content.append(request.getServerPort());
            content.append("\");\n");
            content.append("localStorage.setItem(\"sslEnabled\", \"");
            content.append(request.isSecure());
            content.append("\");\n");
            content.append("localStorage.setItem(\"deviceToken\", \"");
            String deviceToken = null;
            Optional<Cookie[]> cookies = Optional.ofNullable(((HttpServletRequest) request).getCookies());
            Cookie cookie = Arrays.stream(cookies.orElse(new Cookie[0])).filter(c -> c.getName().equals("deviceToken")).findFirst().orElse(null);
            if (cookie != null) {
                deviceToken = cookie.getValue();
            } else {
                deviceToken = session.nextString();
                ((HttpServletResponse) response).addCookie(new Cookie("deviceToken", deviceToken));
            }
            content.append(deviceToken);
            content.append("\");\n");
            content.append("</script>\n");
            if(s != null && s.contains(BODY_TAG)) {
                content.append(s.substring(s.indexOf(BODY_TAG) + BODY_TAG.length()));
            }
            response.setContentLength(content.length());
            try (PrintWriter writer = new PrintWriter(response.getOutputStream())) {
                writer.print(content.toString());
            }
            response.reset();
            response.flushBuffer();
        } else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }


}
