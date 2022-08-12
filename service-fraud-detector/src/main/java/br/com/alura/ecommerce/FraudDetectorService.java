package br.com.alura.ecommerce;

import br.com.alura.database.LocalDatabase;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FraudDetectorService implements ConsumerService<Order> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class.getName());

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private final LocalDatabase database;

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    public FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists("create table if not exists Orders (" +
                "uuid varchar(200) primary key," +
                "is_fraud boolean)");
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> consumerRecord) throws ExecutionException, InterruptedException, SQLException {
        var value = consumerRecord.value();

        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing new order, checking for a fraud");
        LOGGER.info("{}", consumerRecord.key());
        LOGGER.info("{}", value);
        LOGGER.info("{}", consumerRecord.partition());
        LOGGER.info("{}", consumerRecord.offset());

        var order = value.getPayload();

        if (wasProcessed(order)) {
            LOGGER.info("Order {} was already processed", order.getOrderId());
            return;
        }

        if (isFraud(order)) {
            database.update("insert into Orders (uuid, is_fraud) values (?, true)", order.getOrderId());
            LOGGER.warn("Order is a fraud!!!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), value.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        } else {
            database.update("insert into Orders (uuid, is_fraud) values (?, false)", order.getOrderId());
            LOGGER.info("Aproved: {}", order);
            orderDispatcher.send("ECOMMERCE_ORDER_APROVED", order.getEmail(), value.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
