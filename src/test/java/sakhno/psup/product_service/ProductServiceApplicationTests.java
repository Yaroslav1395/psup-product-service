package sakhno.psup.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = {"classpath:application.yaml", "classpath:bootstrap.yaml"})
class ProductServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
