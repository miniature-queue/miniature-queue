package com.github.mlk.queue.rabbitmq.implementation;

import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.QueueType;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RabbitMqServerImplementation implements ServerImplementation {
    private final Logger log = Logger.getLogger(getClass().getName());

    private final ConnectionFactory factory;
    private final boolean autoAck;
    private Connection connection;
    private ThreadLocal<Channel> channels = new ThreadLocal<>();
    private ReentrantLock lock = new ReentrantLock();


    public RabbitMqServerImplementation(ConnectionFactory factory, boolean autoAck) {
        this.factory = factory;
        this.autoAck = autoAck;
    }

    @Override
    public void publish(Queue queue, byte[] message) throws QueueException {
        if(queue.queueTypeHint().equals(QueueType.WORKER_QUEUE)) {
            try {
                Channel channel = getChannel();

                channel.queueDeclare(queue.value(), false, false, false, null);

                channel.basicPublish("", queue.value(), null, message);
            } catch (IOException | TimeoutException ioe) {
                close();
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), ioe);
            }
        } else  {
            try {
                Channel channel = getChannel();

                channel.exchangeDeclare(queue.value(), "fanout");

                channel.basicPublish(queue.value(), "" , null, message);
            } catch (IOException | TimeoutException ioe) {
                close();
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), ioe);
            }
        }
    }

    @Override
    public void listen(Queue queue, Function<byte[], Boolean> action) throws QueueException {
        try {
            Channel channel = getConnection().createChannel();

            String queueName = queue.value();

            if(queue.queueTypeHint().equals(QueueType.WORKER_QUEUE)) {
                channel.queueDeclare(queueName, false, false, false, null);
            } else {
                channel.exchangeDeclare(queue.value(), "fanout");
                queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, queue.value(), "");
            }

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    if (action.apply(body)) {
                        if(!autoAck) {
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        }
                    }

                }
            };

            channel.basicConsume(queueName, autoAck, consumer);
        } catch (IOException | TimeoutException ioe) {
            close();
            throw new QueueException("failed to listen to queue: " + queue.value(), ioe);
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
