package sakhno.psup.product_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("api/v1/product-service/test")
public class TestController {
    private final Random random = new Random();
    private final List<Runnable> exceptionThrowers = List.of(
            () -> { throw new RuntimeException("Simulated RuntimeException"); },
            () -> { throw new NullPointerException("Simulated NullPointerException"); },
            () -> { throw new IllegalStateException("Simulated IllegalStateException"); },
            () -> { throw new UncheckedIOException(new IOException("Simulated IOException")); },
            () -> { throw new RuntimeException(new TimeoutException("Simulated TimeoutException")); },
            () -> { throw new RuntimeException(new SocketTimeoutException("Simulated SocketTimeoutException")); },
            () -> { throw new RuntimeException(new ConnectException("Simulated ConnectException")); },
            () -> { throw new RuntimeException(new SQLException("Simulated SQLException")); },
            () -> { throw new UnsupportedOperationException("Simulated UnsupportedOperationException"); },
            () -> { throw new StackOverflowError("Simulated StackOverflowError"); }
    );

    @GetMapping("/ok")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping
    public ResponseEntity<String> test() {
        int index = random.nextInt(exceptionThrowers.size());
        exceptionThrowers.get(index).run();
        return ResponseEntity.ok("This will never be returned");
    }

    @GetMapping("/timeout")
    public ResponseEntity<String> timeout() {
        System.out.println("timeout");
        try {
            Thread.sleep(5000); // Спим 5 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Interrupted");
        }
        return ResponseEntity.ok("Completed without timeout");
    }

    @GetMapping("/bad-request")
    public ResponseEntity<String> badRequest() {
        return ResponseEntity.badRequest().body("Некорректный запрос");
    }

    @GetMapping("/unprocessable-entity")
    public ResponseEntity<String> unprocessableEntity() {
        return ResponseEntity.unprocessableEntity().body("Некорректный запрос");
    }

    @GetMapping("/forbidden")
    public ResponseEntity<String> forbidden() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Доступ запрещён");
    }

    @GetMapping("/not-found")
    public ResponseEntity<String> notFound() {
        System.out.println("Not found");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Ресурс не найден");
    }

    @GetMapping("/unauthorized")
    public ResponseEntity<String> unauthorized() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Пользователь не авторизован");
    }
}
