package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.Message;
import java.io.IOException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    void parse(ConsumerRecord<String, Message<T>> consumerRecord) throws IOException;

    String getTopic();

    String getConsumerGroup();

}
