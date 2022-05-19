package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher<T> implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDispatcher.class.getName());
    private final KafkaProducer<String, T> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // Local onde o kafka tá rodando
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Serializador de String para bytes
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());

        return properties;
    }

    public void send(String topic, String key, T value) throws ExecutionException, InterruptedException {
        ProducerRecord<String, T> kafkaRecord = new ProducerRecord<>(topic, key, value);

        Callback callback = (data, ex) -> {
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
        };


        producer.send(kafkaRecord, callback).get();
    }

    @Override
    public void close() {
        producer.close();
    }
}