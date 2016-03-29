package com.github.mlk.queue.rabbitmq;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.rabbitmq.implementation.RabbitMqServerImplementation;

public class RabbitMqServer extends Server {

    private final String host;

    public RabbitMqServer(String host) {
        this.host = host;
    }

    @Override
    protected ServerImplementation getImplementation() {
        return new RabbitMqServerImplementation(host);
    }
}
