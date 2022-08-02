package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.KafkaService;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LogService logService = new LogService();
        try (KafkaService<String> kafkaService = new KafkaService<>(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        LOGGER.info("------------------------------------------------------");
        LOGGER.info("Logging: " +  record.topic());
        LOGGER.info(record.key());
        LOGGER.info(String.valueOf(record.value()));
        LOGGER.info(String.valueOf(record.partition()));
        LOGGER.info(String.valueOf(record.offset()));
    }

}
