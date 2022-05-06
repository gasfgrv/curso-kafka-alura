package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class FraudDetectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class.getName());

    public static void main(String[] args) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties())) {
            consumer.subscribe(Collections.singletonList("ECOMMERCE_NEW_ORDER"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    LOGGER.warn("Foram encontrados " + records.count() + " registros");
                }

                records.forEach(consumerRecord -> {
                    LOGGER.info("Processing new order, checking for a fraud");
                    LOGGER.info(consumerRecord.key());
                    LOGGER.info(consumerRecord.value());
                    LOGGER.info(String.valueOf(consumerRecord.partition()));
                    LOGGER.info(String.valueOf(consumerRecord.offset()));
                });
            }
        }
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, FraudDetectorService.class.getSimpleName() + UUID.randomUUID());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); // Recomeda-se essa configuração, para que o consumer trabalhe de um registro por vez mandando a confirmação de leitura da mensagem, para evitar algum problema em durante o rebaleceamento.
        return properties;
    }

}
