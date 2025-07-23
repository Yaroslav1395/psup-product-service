package sakhno.psup.product_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import sakhno.psup.product_service.exceptions.kafka.NonRetryableException;
import sakhno.psup.product_service.exceptions.kafka.RetryableException;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Profile({"local", "test", "prod"})
public class KafkaConfig {
    private final Environment environment;

    /**
     * Метод создает словарь с конфигурацией для kafka consumer
     * @return - словарь с настройками
     */
    @Bean
    ConsumerFactory<String, Object> consumerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put("spring.json.type.mapping",
                environment.getProperty("spring.kafka.consumer.properties.spring.json.type.mapping"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                environment.getProperty("spring.kafka.consumer.auto-offset-reset"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Создает фабрику Kafka Listener с обработкой ошибок:
     * - Повторная попытка 3 раза с задержкой 3 секунды
     * - Отправка в DLT при ошибке
     * - Указание типов ошибок для повторной и не повторной обработки
     * @param consumerFactory - фабрика для создания Kafka Consumer
     * @param kafkaTemplate - шаблон для отправки сообщений в DLT
     * @return настроенная фабрика Kafka Listener
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory, KafkaTemplate kafkaTemplate) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(3000, 3));
        errorHandler.addNotRetryableExceptions(NonRetryableException.class);
        errorHandler.addRetryableExceptions(RetryableException.class);
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Метод создает словарь с конфигурацией для kafka producer
     * @return - словарь с настройками
     */
    @Bean
    public Map<String, Object> producerConfigs(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.producer.key-serializer"));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.producer.value-serializer"));
        config.put(ProducerConfig.ACKS_CONFIG,
                environment.getProperty("spring.kafka.producer.acks"));
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.delivery-timeout-ms"));
        config.put(ProducerConfig.LINGER_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.linger-ms"));
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.request.timeout.ms"));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
                environment.getProperty("spring.kafka.producer.properties.enable.idempotence"));
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                environment.getProperty("spring.kafka.producer.properties.max.in.flight.requests.per.connection"));
        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                environment.getProperty("spring.kafka.producer.properties.spring.json.trusted.packages"));
        config.put("spring.json.type.mapping",
                environment.getProperty("spring.kafka.producer.properties.spring.json.type.mapping"));
        return config;
    }

    /**
     * Метод создает словарь с конфигурацией для kafka producer
     * @return - словарь с настройками
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Создает и настраивает бин KafkaAdmin — управляющий клиент для работы с Kafka.
     * KafkaAdmin используется Spring Kafka для автоматического создания/управления топиками,
     * проверки доступности брокеров и других административных задач.
     * @return настроенный экземпляр KafkaAdmin с конфигурацией подключения к Kafka брокерам.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        return new KafkaAdmin(configs);
    }

    /**
     * Бин KafkaTemplate для отправки сообщений типа Object с ключом типа String.
     * Использует фабрику продюсера для создания и настройки Kafka-продюсеров.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Бин для создания Kafka-топика с именем "product-test-topic".
     * Топик имеет 3 партиции, 3 реплики и настраиваемое количество реплик, которые должны быть синхронизированы для
     * обеспечения отказоустойчивости.
     */
    @Bean
    public NewTopic createTopic() {
        return TopicBuilder
                .name(KafkaTopicNames.PRODUCT_TEST_TOPIC.getTopicName())
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }


}
