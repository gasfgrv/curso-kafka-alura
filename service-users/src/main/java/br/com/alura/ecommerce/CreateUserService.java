package br.com.alura.ecommerce;

import br.com.alura.database.LocalDatabase;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserService implements ConsumerService<Order> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserService.class.getName());
    private final LocalDatabase database;

    public static void main(String[] args) {
        new ServiceRunner(CreateUserService::new).start(1);
    }

    public CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("create table if not exists Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        Message<Order> message = record.value();
        Order order = message.getPayload();

        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing new order, checking for new user");
        LOGGER.info(order.toString());

        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        database.update("insert into Users (uuid, email)" +
                " values (?, ?)", uuid, email);
        LOGGER.info("User uuid and " + email + "added");
    }

    private boolean isNewUser(String email) throws SQLException {
        ResultSet results = database.query("select uuid, email from Users " +
                "where email = ? limit 1", email);
        return !results.next();

    }
}
