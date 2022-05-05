package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderMain.class.getName());

    public static void main(String[] args) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties())) {
            String value = "123, 456, 500.00";
            ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", value, value);
            producer.send(kafkaRecord, (data, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }

                String logMessage = String.format("tópico: %s:::partition %d/ offset %d/ timestamp %d",
                        data.topic(),
                        data.partition(),
                        data.offset(),
                        data.timestamp());

                LOGGER.info(logMessage);
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // Local onde o kafka tá rodando
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Serializador de String para bytes
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

}
