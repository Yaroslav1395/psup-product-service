package sakhno.psup.product_service.services.kafka.kafka_test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sakhno.psup.product_service.config.kafka.KafkaTopicNames;
import sakhno.psup.product_service.dto.ManufactureTestDto;
import sakhno.psup.product_service.events.consumer.ProductTestEvent;

@Profile({"local", "test", "prod"})
@Component
@KafkaListener(topics = "store-test-topic")
@RequiredArgsConstructor
@Slf4j
public class StorageTestEventHandler {
    private final ManufactureTestEventProducer manufactureTestEventProducer;

    @KafkaHandler
    public void handle(@Payload ProductTestEvent productTestEvent, @Header("messageId") String id,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Из очереди store-test-topic получено сообщение: {}. Ключ сообщения: {}. ID сообщения: {}",
                productTestEvent.getMessage(), messageKey, id);

        log.info("Преобразование сообщения в DTO");
        ManufactureTestDto manufactureTestDto = ManufactureTestDto.builder()
                .id(productTestEvent.getId())
                .message(productTestEvent.getMessage())
                .build();

        log.info("Отправка сообщения в очередь: {}", KafkaTopicNames.PRODUCT_TEST_TOPIC.getTopicName());
        manufactureTestEventProducer.sendTestMessageToTopicProductTestEvent(manufactureTestDto);
    }
}
