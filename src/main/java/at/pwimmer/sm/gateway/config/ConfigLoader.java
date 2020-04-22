package at.pwimmer.sm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigLoader {

    @Bean
    public GatewayConfig gatewayConfig() {
        return new GatewayConfig("127.0.0.1", 1234, 15000);
    }
}
