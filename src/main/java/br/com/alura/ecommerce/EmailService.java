package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class.getName());


    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        try (KafkaService<String> kafkaService = new KafkaService<>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                String.class,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> consumerRecord) {
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
    }

}
