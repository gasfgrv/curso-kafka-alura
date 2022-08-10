package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingReportService implements ConsumerService<User> {

    public static final Path SOURCE = new File("src/main/resources/report.txt").toPath();
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingReportService.class.getName());

    private final KafkaDispatcher<User> orderDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        new ServiceRunner(ReadingReportService::new).start(5);
    }


    @Override
    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing report for " + record.value());

        Message<User> message = record.value();
        User user = message.getPayload();
        File target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());

        LOGGER.info("File created: " + target.getAbsolutePath());
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }
}
