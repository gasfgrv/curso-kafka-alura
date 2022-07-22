package br.com.alura.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserService.class.getName());
    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite::resource:users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("create table if not exists Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    public static void main(String[] args) throws SQLException {
        CreateUserService createUserService = new CreateUserService();
        try (KafkaService<Order> kafkaService = new KafkaService<>(CreateUserService .class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        Message<Order> message = record.value();
        Order order = message.getPayload();

        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing new order, checking for new user");
        LOGGER.info(order.toString());

        if(isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }

    }

    private void insertNewUser(String email) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("insert into Users (uuid, email)" +
                " values (?, ?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();

        LOGGER.info("User uuid and " + email + "added");
    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement exists = connection.prepareStatement("select uuid, email from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        ResultSet results = exists.executeQuery();
        return !results.next();

    }
}
