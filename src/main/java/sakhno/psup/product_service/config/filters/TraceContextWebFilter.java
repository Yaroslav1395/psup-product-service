package sakhno.psup.product_service.config.filters;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Фильтр, внедряющий traceparent (идентификатор трассировки) в контекст Reactor-а,
 * если он передан в HTTP-заголовке.
 */
@Component
public class TraceContextWebFilter implements WebFilter {

    /**
     * Метод фильтрации, который вызывается для каждого HTTP-запроса.
     * Если в заголовке присутствует traceparent, он добавляется в контекст Reactor.
     *
     * @param exchange - текущий HTTP-запрос/ответ.
     * @param chain - цепочка фильтров, по которой должен пройти запрос.
     * @return Mono<Void> - реактивный поток, сигнализирующий об окончании обработки запроса.
     */
    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String traceParent = exchange.getRequest().getHeaders().getFirst("traceparent");

        if (traceParent != null) {
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put("traceparent", traceParent));
        } else {
            return chain.filter(exchange);
        }
    }
}
