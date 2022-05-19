package br.com.alura.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderMain.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
             KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>()) {

            String userId = UUID.randomUUID().toString();
            String orderId = UUID.randomUUID().toString();
            BigDecimal amount = BigDecimal.valueOf(Math.random() * 5000 + 1).setScale(2, RoundingMode.HALF_UP);

            Order order = new Order(userId, orderId, amount);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

            String email = "Thank you for your order! We are processing your order!";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
        }
    }


}
