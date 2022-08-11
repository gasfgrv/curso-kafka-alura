package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.Message;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    void parse(ConsumerRecord<String, Message<T>> consumerRecord) throws Exception;

    String getTopic();

    String getConsumerGroup();

}
