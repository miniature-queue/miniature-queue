package com.github.mlk.queue.rabbitmq;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.rabbitmq.implementation.RabbitMqServerImplementation;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqServer extends Server {

    private final ConnectionFactory factory;

    public RabbitMqServer(String host) {
        this.factory = new ConnectionFactory();
        factory.setHost(host);
    }

    public RabbitMqServer(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    protected ServerImplementation getImplementation() {
        return new RabbitMqServerImplementation(factory);
    }
}
