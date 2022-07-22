package br.com.alura.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaDispatcher<T> implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDispatcher.class.getName());
    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); // Local onde o kafka tá rodando
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Serializador de String para bytes
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    public void send(String topic, String key, CorrelationId correlationId, T payload) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(topic, key, correlationId, payload);
        future.get();
    }

    Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId correlationId, T payload) {
        Message<T> value = new Message<>(correlationId, payload);
        ProducerRecord<String, Message<T>> kafkaRecord = new ProducerRecord<>(topic, key, value);

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


        Future<RecordMetadata> future = producer.send(kafkaRecord, callback);
        return future;
    }

    @Override
    public void close() {
        producer.close();
    }
}
