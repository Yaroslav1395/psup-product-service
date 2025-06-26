package sakhno.psup.product_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/product-service/test")
public class TestController {
    private Integer count = 0;

    @GetMapping
    public ResponseEntity<String> test() {
        count++;
        System.out.println("Запрос " + count);
        throw new RuntimeException("Simulated failure");
    }

    @GetMapping("/timeout")
    public ResponseEntity<String> timeout() {
        try {
            Thread.sleep(5000); // Спим 5 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Interrupted");
        }
        return ResponseEntity.ok("Completed without timeout");
    }

    @GetMapping("/ok")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("OK");
    }
}
