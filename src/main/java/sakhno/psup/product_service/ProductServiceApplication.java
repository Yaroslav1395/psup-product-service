package sakhno.psup.product_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "sakhno.psup.product_service.repositories")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

    @PostConstruct
    public  void  init () {
        Hooks.enableAutomaticContextPropagation();
    }

}
