package br.com.alura.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderMain.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher kafkaDispatcher = new KafkaDispatcher()) {
            String key = UUID.randomUUID().toString();
            String value = key + "123, 456, 500.00";
            kafkaDispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

            String email = "Thank you for your order! We are processing your order!";
            kafkaDispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
        }
    }


}
