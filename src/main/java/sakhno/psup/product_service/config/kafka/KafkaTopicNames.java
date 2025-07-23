package sakhno.psup.product_service.config.kafka;

import lombok.Getter;

@Getter
public enum KafkaTopicNames {

    MANUFACTURE_TEST_TOPIC("manufacture-test-topic"),
    STORE_TEST_TOPIC("store-test-topic"),
    PRODUCT_TEST_TOPIC("product-test-topic");

    private final String topicName;

    KafkaTopicNames(String topicName) {
        this.topicName = topicName;
    }
}
