package com.github.mlk.queue.rabbitmq;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.rabbitmq.implementation.RabbitMqServerImplementation;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqServer extends Server {

    private final RabbitMqServerImplementation implementation;

    public RabbitMqServer(String host) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        implementation = new RabbitMqServerImplementation(factory);

    }

    public RabbitMqServer(ConnectionFactory factory) {
        implementation = new RabbitMqServerImplementation(factory);
    }

    @Override
    protected ServerImplementation getImplementation() {
        return implementation;
    }
}
