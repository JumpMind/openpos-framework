package org.jumpmind.pos.core.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class DefaultHttpConnectionService implements IHttpConnectionService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    CloseableHttpClient httpclient = HttpClients.createDefault();

    @Override
    public byte[] get(String url) throws IOException {
        byte[] data;
        HttpGet httpGet = new HttpGet(url);
        logRequest(httpGet);
        try(CloseableHttpResponse response = httpclient.execute(httpGet)){
            logResponse(response);
            if( response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toByteArray(entity);
                EntityUtils.consume(entity);
            } else {
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            }

        } catch ( Exception e ){
            throw new IOException("Http get failed to get url: " + url, e);
        }

        return data;
    }

    private void logRequest(HttpRequestBase request) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("==========================request begin=============================================");
            logger.info("URI         : {}", request.getURI());
            logger.info("Method      : {}", request.getMethod());
            logger.info("Headers     : {}", request.getAllHeaders());
            logger.info("==========================request end===============================================");
        }
    }

    private void logResponse(HttpResponse response) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("============================response begin==========================================");
            logger.info("Status line  : {}", response.getStatusLine());
            logger.info("Headers      : {}", response.getAllHeaders());
            logger.info("============================response end============================================");
        }
    }
}
