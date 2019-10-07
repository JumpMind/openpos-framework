package org.jumpmind.pos.core.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DefaultHttpConnectionService implements IHttpConnectionService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    CloseableHttpClient httpclient = HttpClients.createDefault();

    @Value("${remote.image.request.connection.timeout:2000}")
    private int connectTimeout;

    @Value("${openpos.http.client.socket.timeout:2000}")
    private int socketTimeout;

    @Value("${openpos.http.client.connect.timeout:2000}")
    private int connectionRequestTimeout;

    @Override
    public byte[] get(String url) throws IOException {
        byte[] data;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        logRequest(httpGet);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            logResponse(response);
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toByteArray(entity);
                EntityUtils.consume(entity);
            } else {
                throw new HttpResponseException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
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