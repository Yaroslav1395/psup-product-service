package sakhno.psup.product_service.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Данная конфигурация предназначена для использования WebClient без поддержки LoadBalancer. То есть запуск без Eureka.
 * Конфигурация срабатывает в случае если сервис запущен с профилем "local" или "default".
 */
@Configuration
@Profile({"local", "default"})
public class WebClientWithoutLoadBalancerConfig {
    @Value("${manufacture.service.base.url}")
    private String manufactureBaseUrl;

    /**
     * Создает бин {@link WebClient.Builder}, настроенный для реактивного взаимодействия с другими микросервисами.
     * @return настроенный {@link WebClient.Builder} без поддержки LoadBalancer
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Создает бин {@link WebClient}, настроенный для реактивного взаимодействия с сервисом хранения (STORAGE-SERVICE).
     *
     * @param builder конфигурационный {@link WebClient.Builder} без поддержки LoadBalancer.
     * @return настроенный {@link WebClient} для обращения к STORAGE-SERVICE
     */
    @Bean
    public WebClient manufactureServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(manufactureBaseUrl)
                .build();
    }
}
