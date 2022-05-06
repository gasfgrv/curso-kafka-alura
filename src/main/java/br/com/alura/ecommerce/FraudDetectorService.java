package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FraudDetectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class.getName());

    public static void main(String[] args) {
        FraudDetectorService fraudDetectorService = new FraudDetectorService();
        try (KafkaService kafkaService = new KafkaService(FraudDetectorService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER", fraudDetectorService::parse)) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("Processing new order, checking for a fraud");
        LOGGER.info(consumerRecord.key());
        LOGGER.info(consumerRecord.value());
        LOGGER.info(String.valueOf(consumerRecord.partition()));
        LOGGER.info(String.valueOf(consumerRecord.offset()));
    }

}
