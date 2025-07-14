package sakhno.psup.product_service.utils;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class TraceContextUtils {
    private final Tracer tracer;

    /**
     * Оборачивает выполнение логики в контекст, извлекая значение traceparent из Reactor Context.
     * <p>
     * Если traceparent присутствует в контексте, он будет передан в callback-функцию. Это позволяет
     * сохранить трассировочную информацию при вызовах WebClient или других асинхронных операций.
     *
     * @param callback функция, принимающая traceparent (может быть null) и возвращающая Mono с результатом
     * @param <T> тип возвращаемого результата
     * @return Mono, обёрнутое с использованием traceparent из контекста
     */
    public <T> Mono<T> withTraceParent(Function<String, Mono<T>> callback) {
        return Mono.deferContextual(ctx -> {
            String traceParent = ctx.getOrDefault("traceparent", null);
            log.info("Trace parent {}", traceParent);
            return callback.apply(traceParent);
        });
    }

    /**
     * Устанавливает traceparent в заголовки HTTP-запроса, если он не равен null. Заменяет spanId на текущий.
     * <p>
     * Используется для передачи трассировочной информации в исходящих HTTP-запросах
     * (например, через WebClient), чтобы поддерживать распределённую трассировку.
     *
     * @param traceParent значение traceparent (может быть null)
     * @param headers объект заголовков, в который будет установлен traceparent
     */
    public void setTraceToHeaders(String traceParent, HttpHeaders headers) {
        String spanId = tracer.currentSpan().context().spanId();
        if (traceParent != null) {
            String[] parts = traceParent.split("-");
            if (parts.length == 4) {
                String newTraceParent = String.format("%s-%s-%s-%s", parts[0], parts[1], spanId, parts[3]);
                headers.set("traceparent", newTraceParent);
            } else {
                // fallback, если формат не тот
                headers.set("traceparent", traceParent);
            }
        }
    }
}
