package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FraudDetectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FraudDetectorService fraudDetectorService = new FraudDetectorService();
        try (KafkaService<Order> kafkaService = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudDetectorService::parse,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing new order, checking for a fraud");
        LOGGER.info(record.key());
        LOGGER.info(String.valueOf(record.value()));
        LOGGER.info(String.valueOf(record.partition()));
        LOGGER.info(String.valueOf(record.offset()));

        Message<Order> value = record.value();
        Order order = value.getPayload();

        if (isFraud(order)) {
            LOGGER.warn("Order is a fraud!!!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), value.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        } else {
            LOGGER.info("Aproved: " + order);
            orderDispatcher.send("ECOMMERCE_ORDER_APROVED", order.getEmail(), value.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
