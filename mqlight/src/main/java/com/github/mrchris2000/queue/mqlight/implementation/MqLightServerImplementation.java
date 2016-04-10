package com.github.mrchris2000.queue.mqlight.implementation;

import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.QueueType;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.ibm.mqlight.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Logger;

public class MqLightServerImplementation implements ServerImplementation {
    private final Logger log = Logger.getLogger(getClass().getName());

    private NonBlockingClient client;
    private ReentrantLock lock = new ReentrantLock();


    public MqLightServerImplementation(NonBlockingClient client) {
        this.client = client;
    }

    @Override
    public void publish(Queue queue, byte[] message) throws QueueException {
        if(queue.queueTypeHint().equals(QueueType.WORKER_QUEUE)) {
            try {
                //ToDo: Implement other queue semantics
                client.send(queue.value(), "Booooooooooooooo!", null, null);
            } catch (Exception e) {
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), e);
            }
        } else  {
            try {
                String decoded = new String(message, StandardCharsets.UTF_8);
                client.send(queue.value(), decoded, null);
            } catch (Exception ioe) {
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), ioe);
            }
        }
    }

    @Override
    public void listen(Queue queue, Function<byte[], Boolean> action) throws QueueException {
        System.out.println("Registering listener");
        client.subscribe(queue.value(), new DestinationAdapter<Void>() {
            public void onMessage(NonBlockingClient client, Void context, Delivery delivery) {
                System.out.println("Message received...");
                System.out.println(delivery.getType());
                switch (delivery.getType()) {
                    case BYTES:
                        BytesDelivery bd = (BytesDelivery)delivery;
                        System.out.println(bd.getData());
                        break;
                    case STRING:
                        StringDelivery sd = (StringDelivery)delivery;
                        System.out.println(sd.getData());
                        break;
                    case JSON:
                        JsonDelivery jd = (JsonDelivery)delivery;
                        System.out.println(jd.getRawData());
                        break;
                }

            }
        }, new CompletionListener<Void>() {
               @Override
               public void onSuccess(NonBlockingClient c, Void ctx) {
                   System.out.println("Subscribed to: "+ queue.value());
               }
               @Override
               public void onError(NonBlockingClient c, Void ctx, Exception exception) {
                   System.out.println("Error:");
                   exception.printStackTrace();
               }
        }, null);
    }

    @Override
    public void close() {

    }

    /*
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

    */

    private NonBlockingClient getConnection() throws IOException, TimeoutException {
        lock.lock();
        try {
            if (client == null) {
                throw new QueueException("asd", null);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }
}
