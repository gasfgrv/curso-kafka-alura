package br.com.alura.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingReportService {

    public static final Path SOURCE = new File("src/main/resources/report.txt").toPath();
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadingReportService.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ReadingReportService readingReportService = new ReadingReportService();
        try (KafkaService<User> kafkaService = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "ECOMMERCE_USER_GENERATE_READING_REPORT",
                readingReportService::parse,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private final KafkaDispatcher<User> orderDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        LOGGER.info("--------------------------------------------");
        LOGGER.info("Processing report for " + record.value());

        Message<User> message = record.value();
        User user = message.getPayload();
        File target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());

        LOGGER.info("File created: " + target.getAbsolutePath());
    }

}
