package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

public class LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class.getName());

    public static void main(String[] args) {
        LogService logService = new LogService();
        try (KafkaService<String> kafkaService = new KafkaService<>(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                String.class,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("------------------------------------------------------");
        LOGGER.info("Logging: " +  consumerRecord.topic());
        LOGGER.info(consumerRecord.key());
        LOGGER.info(consumerRecord.value());
        LOGGER.info(String.valueOf(consumerRecord.partition()));
        LOGGER.info(String.valueOf(consumerRecord.offset()));
    }

}
