package org.jumpmind.pos.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(Collections.singletonList(apiTokenScheme()))
                .securityContexts(Collections.singletonList(apiTokenContext()))
                .select()
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiKey apiTokenScheme() {
        return new ApiKey("APIToken", "X-API-Token", ApiKeyVehicle.HEADER.name());
    }

    private SecurityContext apiTokenContext() {
        return SecurityContext.builder().securityReferences(Collections.singletonList(apiTokenReference())).build();
    }

    private SecurityReference apiTokenReference() {
        AuthorizationScope[] authScopes = new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")};
        return new SecurityReference("APIToken", authScopes);
    }
}
