package org.jumpmind.pos.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class OpenposManagementServer {
    public static void main(String[] args) throws Exception {
        System.out.println("OpenposManagementServer working dir: " + System.getProperty("user.dir"));
        SpringApplication.run(OpenposManagementServer.class, args);
    }
}
