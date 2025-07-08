package sakhno.psup.product_service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public class TraceContextUtils {

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
    public static <T> Mono<T> withTraceParent(Function<String, Mono<T>> callback) {
        return Mono.deferContextual(ctx -> {
            String traceParent = ctx.getOrDefault("traceparent", null);
            log.info("Trace parent {}", traceParent);
            return callback.apply(traceParent);
        });
    }

    /**
     * Устанавливает traceparent в заголовки HTTP-запроса, если он не равен null.
     * <p>
     * Используется для передачи трассировочной информации в исходящих HTTP-запросах
     * (например, через WebClient), чтобы поддерживать распределённую трассировку.
     *
     * @param traceParent значение traceparent (может быть null)
     * @param headers объект заголовков, в который будет установлен traceparent
     */
    public static void setTraceToHeaders(String traceParent, HttpHeaders headers) {
        if (traceParent != null) {
            headers.set("traceparent", traceParent);
        }
    }
}
