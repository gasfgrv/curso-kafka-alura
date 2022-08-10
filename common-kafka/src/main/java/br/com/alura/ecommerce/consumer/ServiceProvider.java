package br.com.alura.ecommerce.consumer;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;

    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Void call() throws Exception {
        ConsumerService<T> myService = factory.create();

        try (KafkaService<String> service = new KafkaService(myService.getConsumerGroup(),
                myService.getTopic(),
                myService::parse,
                new HashMap<>())) {
            service.run();
        }

        return null;
    }
}
