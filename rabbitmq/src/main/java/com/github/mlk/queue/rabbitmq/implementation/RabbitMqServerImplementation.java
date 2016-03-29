package com.github.mlk.queue.rabbitmq.implementation;

import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class RabbitMqServerImplementation implements ServerImplementation {
    private final ConnectionFactory factory = new ConnectionFactory();

    public RabbitMqServerImplementation(String hostname) {
        factory.setHost(hostname);
    }

    @Override
    public void publish(String queueName, byte[] message) throws QueueException {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicPublish("", queueName, null, message);

            channel.close();
            connection.close();
        } catch (IOException | TimeoutException ioe) {
            throw new QueueException("failed to enqueue onto queue: " + queueName, ioe);
        }
    }

    @Override
    public void listen(String queueName, Function<byte[], Boolean> action) throws QueueException {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    if (action.apply(body)) {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }

                }
            };

            channel.basicConsume(queueName, false, consumer);
        } catch (IOException | TimeoutException ioe) {
            throw new QueueException("failed to enqueue onto queue: " + queueName, ioe);
        }
    }
}
