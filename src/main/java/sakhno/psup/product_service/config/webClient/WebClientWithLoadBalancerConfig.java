package sakhno.psup.product_service.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Данная конфигурация предназначена для использования WebClient с поддержкой LoadBalancer. Eureka должна быть доступна.
 * Конфигурация срабатывает в случае если сервис запущен с профилем "test" или "prod".
 */
@Configuration
@Profile({"test", "prod"})
public class WebClientWithLoadBalancerConfig {
    @Value("${manufacture.service.base.url}")
    private String manufactureBaseUrl;

    /**
     * Создает бин {@link WebClient.Builder}, настроенный для реактивного взаимодействия с другими микросервисами.
     * <p>
     * Благодаря аннотации {@code @LoadBalanced}, WebClient будет использовать клиентскую балансировку нагрузки
     * с разрешением адресов через Eureka.
     *
     * @return настроенный {@link WebClient.Builder} с поддержкой LoadBalancer
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Создает бин {@link WebClient}, настроенный для реактивного взаимодействия с сервисом хранения (STORAGE-SERVICE).
     *
     * @param builder конфигурационный {@link WebClient.Builder} с поддержкой LoadBalancer (через Eureka)
     * @return настроенный {@link WebClient} для обращения к STORAGE-SERVICE
     */
    @Bean
    public WebClient manufactureServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(manufactureBaseUrl)
                .build();
    }
}
