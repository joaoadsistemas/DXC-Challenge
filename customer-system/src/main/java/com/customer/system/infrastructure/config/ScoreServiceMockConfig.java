package com.customer.system.infrastructure.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.score-service.mock", havingValue = "true", matchIfMissing = true)
public class ScoreServiceMockConfig {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceMockConfig.class);

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer scoreMockServer() {
        var server = new WireMockServer(8081);
        configureStubs(server);
        log.info("Score service mock server configured on port 8081");
        return server;
    }

    private void configureStubs(WireMockServer server) {
        server.stubFor(WireMock.get(WireMock.urlPathMatching("/scores/\\d{11}"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "cpf": "{{request.pathSegments.[1]}}",
                                  "score": 750,
                                  "classification": "LOW_RISK"
                                }
                                """)));
    }

    @PreDestroy
    public void onDestroy() {
        log.info("Score service mock server stopped");
    }
}
