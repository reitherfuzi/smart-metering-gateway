package at.pwimmer.sm.gateway;

import at.pwimmer.sm.gateway.config.GatewayConfig;
import at.pwimmer.sm.gateway.tasks.SmartReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        ExecutorService service = Executors.newCachedThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(service::shutdownNow));
        return service;
    }

    @Bean
    public SmartReader smartReader(ExecutorService executorService, GatewayConfig gatewayConfig) {
        SmartReader reader = new SmartReader(gatewayConfig.getHost(), gatewayConfig.getPort(), gatewayConfig.getTimeout());
        executorService.execute(reader);

        Runtime.getRuntime().addShutdownHook(new Thread(reader::shutdown));
        return reader;
    }
}
