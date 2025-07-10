package kz.jarkyn.backend.export.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GotenbergWebClientConfig {
    @Bean
    public WebClient gotenbergWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:3000") // Replace with your Gotenberg URL
                .build();
    }
}