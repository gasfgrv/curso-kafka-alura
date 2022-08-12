package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService implements ConsumerService<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class.getName());


    public static void main(String[] args) {
        new ServiceRunner<>(EmailService::new).start(5);
    }

    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    public void parse(ConsumerRecord<String, Message<String>> consumerRecord) {
        var key = consumerRecord.key();
        var value = String.valueOf(consumerRecord.value());
        var partition = String.valueOf(consumerRecord.partition());
        var offset = String.valueOf(consumerRecord.offset());

        LOGGER.info("Sending email");
        LOGGER.info(key);
        LOGGER.info(value);
        LOGGER.info(partition);
        LOGGER.info(offset);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        LOGGER.info("Email sent");
    }

}
