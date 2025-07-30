package sakhno.psup.product_service.config.exception;

import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import reactor.core.publisher.Mono;
import sakhno.psup.product_service.dto.ResponseDto;
import sakhno.psup.product_service.exceptions.all.DuplicateEntityException;
import sakhno.psup.product_service.exceptions.all.EntityNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Метод обрабатывает ошибки валидации параметров запроса
     * @param ex - исключение при валидации параметров запроса
     * @return - ответ с сообщением об ошибке
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Mono<ResponseEntity<ResponseDto<Object>>> handlerMethodValidationException(HandlerMethodValidationException ex) {
        String errors = ex.getValueResults().stream()
                .flatMap(valueResult -> valueResult.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("Возникла ошибка при валидации параметров запроса: {}", errors);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.validation(errors)));
    }

    /**
     * Метод обрабатывает ошибки валидации объектов запроса
     * @param ex - исключение при валидации объектов запроса
     * @return - ответ с сообщением об ошибке
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ResponseDto<Object>>> handleBindException(WebExchangeBindException  ex) {
        String errors = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + ":" + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("Возникла ошибка при валидации объекта запроса: {}", errors);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.validation(errors)));
    }

    /**
     * Метод обрабатывает ошибки валидации ограничений при записи в базу данных
     * @param ex - ошибка при записи в базу
     * @return - ответ с сообщением об ошибке
     */
    @ExceptionHandler(R2dbcDataIntegrityViolationException.class)
    public Mono<ResponseEntity<ResponseDto<Object>>> handleR2dbcDataIntegrityViolationException(R2dbcDataIntegrityViolationException ex) {
        log.error("Возникла ошибка при запросе в базу: {}", ex.getMessage());
        String message = "Нарушение ограничения целостности данных: " + ex.getMessage();
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(ResponseDto.error(message)));
    }

    /**
     * Метод обрабатывает исключения при дублировании записи
     * @param ex - исключение дублирования
     * @return - ответ с сообщением об ошибке
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public Mono<ResponseEntity<ResponseDto<Object>>> handleDuplicateEntityException(DuplicateEntityException ex) {
        log.warn("Нарушение уникальности записи в базе: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fail(ex.getMessage())));
    }

    /**
     * Метод обрабатывает исключения при отсутствии записи
     * @param ex - исключение отсутствия
     * @return - ответ с сообщением об ошибке
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ResponseDto<Object>>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Запись не найдена в базе: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fail(ex.getMessage())));
    }
}
