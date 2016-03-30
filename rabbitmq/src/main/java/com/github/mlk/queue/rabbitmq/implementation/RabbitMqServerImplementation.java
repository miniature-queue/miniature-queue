package com.github.mlk.queue.rabbitmq.implementation;

import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.google.common.base.Function;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RabbitMqServerImplementation implements ServerImplementation {
    private final Logger log = Logger.getLogger(getClass().getName());

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
            close();
            throw new QueueException("failed to enqueue onto queue: " + queueName, ioe);
        }
    }

    @Override
    public void listen(String queueName, final Function<byte[], Boolean> action) throws QueueException {
        try {
            final Channel channel = getChannel();

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
            close();
            throw new QueueException("failed to enqueue onto queue: " + queueName, ioe);
        }
    }

    @Override
    public void close() {
        lock.lock();

        try {
            if(channels.get() != null) {
                channels.get().close();
            }

            getConnection().close();
        } catch (TimeoutException | IOException e) {
            log.log(Level.INFO, "Failed to close connection", e);
        } finally {
            channels.remove();
            connection = null;
            lock.unlock();
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
