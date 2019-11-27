package org.jumpmind.pos.management;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

/**
 * Tests that the server will start with SSL where the certificates are stored
 * in a keystore located in src/test/resources/test-keystore.p12.  Also tests
 * to ensure that the needed SSL properties can be successfully retrieved using
 * spring.profiles.include via the application-enable_ssl.properties and the
 * application-localtest.yml
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"localtest", "enable_ssl"})
public class OpenposManagementServerSSLTest {

    @Autowired
    private DiscoveryServiceController discoveryController;
    
    @LocalServerPort
    private int port;
    
    @Value("${trustStore}")
    String trustStore;

    @Value("${trustStorePassword}")
    String trustStorePassword;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("jasypt.encryptor.password", EncryptionTestConstants.ENCRYPTOR_PASSWORD);
        System.setProperty("jasypt.encryptor.algorithm", EncryptionTestConstants.ENCYPTION_ALGORITHM);
    }
    
    @AfterClass
    public static void afterClass() {
        System.setProperty("jasypt.encryptor.password", "");
        System.setProperty("jasypt.encryptor.algorithm", "");
    }
    
    @Test
    public void contextLoads() throws Exception {
        assertThat(discoveryController).isNotNull();
    }

    @Ignore
    @Test
    public void testPingOverSSL() throws RestClientException, Exception {
        ResponseEntity<String> response = restTemplate().getForEntity("https://localhost:" + port + "/ping", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{ \"pong\": \"true\" }");
    }
    
    RestTemplate restTemplate() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream keyStoreInputStream = classLoader.getResourceAsStream(trustStore);
        if (keyStoreInputStream == null) {
            throw new FileNotFoundException("Could not find file named '" + trustStore + "' in the CLASSPATH");
        }
        
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(keyStoreInputStream, trustStorePassword.toCharArray());
        

        SSLContext sslContext = new SSLContextBuilder()
          .loadTrustMaterial(keystore, new TrustAllStrategy())
          .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom()
          .setSSLSocketFactory(socketFactory)
          .build();
        HttpComponentsClientHttpRequestFactory factory = 
          new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }    
}