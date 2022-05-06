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

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class.getName());

    public static void main(String[] args) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties())) {
            consumer.subscribe(Collections.singletonList("ECOMMERCE_SEND_EMAIL"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    LOGGER.warn("Foram encontrados " + records.count() + " registros");
                }

                records.forEach(consumerRecord -> {
                    LOGGER.info("Sending email");
                    LOGGER.info(consumerRecord.key());
                    LOGGER.info(consumerRecord.value());
                    LOGGER.info(String.valueOf(consumerRecord.partition()));
                    LOGGER.info(String.valueOf(consumerRecord.offset()));

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }

                    LOGGER.info("Email sent");
                });
            }
        }
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, EmailService.class.getSimpleName());
        return properties;
    }

}
