package com.github.mlk.queue.rabbitmq.implementation;

import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class RabbitMqServerImplementation implements ServerImplementation {
    private final ConnectionFactory factory;
    private Connection connection;
    private ThreadLocal<Channel> channels = new ThreadLocal<>();

    private ReentrantLock lock = new ReentrantLock();

    public RabbitMqServerImplementation(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void publish(String queueName, byte[] message) throws QueueException {
        try {
            Channel channel = getChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicPublish("", queueName, null, message);
        } catch (IOException | TimeoutException ioe) {
            throw new QueueException("failed to enqueue onto queue: " + queueName, ioe);
        }
    }

    @Override
    public void listen(String queueName, Function<byte[], Boolean> action) throws QueueException {
        try {
            Channel channel = getChannel();

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

    private Channel getChannel() throws IOException, TimeoutException {
        lock.lock();
        try {
            Channel channel = channels.get();
            if(channel == null) {
                Connection currentConnection = getConnection();
                channel = currentConnection.createChannel();
                channels.set(channel);
            }
            return channel;
        } finally {
            lock.unlock();
        }
    }

    private Connection getConnection() throws IOException, TimeoutException {
        lock.lock();
        try {
            if (connection == null) {
                connection = factory.newConnection();
            }
            return connection;
        } finally {
            lock.unlock();
        }
    }
}