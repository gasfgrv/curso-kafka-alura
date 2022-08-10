package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNewOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNewOrderService.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EmailNewOrderService emailNewOrderService = new EmailNewOrderService();
        try (KafkaService<Order> kafkaService = new KafkaService<>(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                emailNewOrderService::parse,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing new order, preparing email");
        Message<Order> message = record.value();
        LOGGER.info(String.valueOf(message));

        String emailCode = "Thank you for your order! We are processing your order!";
        Order order = message.getPayload();
        CorrelationId id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);
    }

}
