package com.github.mlk.queue.rabbitmq;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.rabbitmq.implementation.RabbitMqServerImplementation;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqServer extends Server {

    private final RabbitMqServerImplementation implementation;

    /** @param host The host to connect to. This will result in a default connection factory with this host.
     * @param autoAck Should the messages be automatically ack'ed. If set then the return from the Function in @Handle methods is ignored
     */
    public RabbitMqServer(String host, boolean autoAck) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        implementation = new RabbitMqServerImplementation(factory, autoAck);

    }

    /** @param factory The connection factory for Rabbit MQ.
     * @param autoAck Should the messages be automatically ack'ed. If set then the return from the Function in @Handle methods is ignored
     */
    public RabbitMqServer(ConnectionFactory factory, boolean autoAck) {
        implementation = new RabbitMqServerImplementation(factory, autoAck);
    }

    /** @param host The host to connect to. This will result in a default connection factory with this host. */
    public RabbitMqServer(String host) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        implementation = new RabbitMqServerImplementation(factory, false);
    }

    /** @param factory The connection factory for Rabbit MQ. */
    public RabbitMqServer(ConnectionFactory factory) {
        this(factory, false);
    }

    @Override
    protected ServerImplementation getImplementation() {
        return implementation;
    }
}
