package sakhno.psup.product_service.services.kafka.kafka_test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sakhno.psup.product_service.config.kafka.KafkaTopicNames;
import sakhno.psup.product_service.dto.ManufactureTestDto;
import sakhno.psup.product_service.events.producer.ManufactureTestEvent;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManufactureTestEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTestMessageToTopicProductTestEvent(ManufactureTestDto manufactureTestDto) {
        log.info("Отработка отправки сообщения в очередь: {}", manufactureTestDto.getMessage());

        Long id = new Random().nextLong();

        log.info("Сгенерирован ID: {}", id);

        ManufactureTestEvent manufactureTestEvent = ManufactureTestEvent.builder()
                .id(id)
                .message(manufactureTestDto.getMessage() + " + product-service")
                .build();

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaTopicNames.PRODUCT_TEST_TOPIC.getTopicName(), id.toString(), manufactureTestEvent);
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        kafkaTemplate.send(record)
                .thenAccept(result -> log.info("Сообщение отправлено с офсетом: {}", result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Сообщение не отправлено", ex);
                    return null;
                });

    }
}
