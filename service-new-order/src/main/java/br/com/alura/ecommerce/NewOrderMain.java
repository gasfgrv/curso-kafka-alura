package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            String email = "user_" + UUID.randomUUID() + "@email.com";

            try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {

                String orderId = UUID.randomUUID().toString();
                BigDecimal amount = BigDecimal.valueOf(Math.random() * 5000 + 1)
                        .setScale(2, RoundingMode.HALF_UP);

                CorrelationId id = new CorrelationId(NewOrderMain.class.getSimpleName());

                Order order = new Order(orderId, amount, email);
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, id, order);
            }
        }
    }


}
