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
import java.util.logging.Level;
import java.util.logging.Logger;

public class MqLightServerImplementation implements ServerImplementation {
    private final Logger logger = Logger.getLogger(getClass().getName());

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
                String decoded = new String(message, StandardCharsets.UTF_8);
                client.send(queue.value(), decoded, null);
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
        logger.log(Level.INFO, "Registering listener");
        client.subscribe(queue.value(), new DestinationAdapter<Void>() {
            public void onMessage(NonBlockingClient client, Void context, Delivery delivery) {
                logger.log(Level.INFO, "Message received...");
                logger.log(Level.INFO, delivery.getType().toString());
                switch (delivery.getType()) {
                    case BYTES:
                        BytesDelivery bd = (BytesDelivery)delivery;
                        logger.log(Level.WARNING, bd.getData().toString());
                        break;
                    case STRING:
                        StringDelivery sd = (StringDelivery)delivery;
                        logger.log(Level.WARNING, sd.getData().toString());
                        break;
                    case JSON:
                        JsonDelivery jd = (JsonDelivery)delivery;
                        logger.log(Level.WARNING, jd.getRawData().toString());
                        break;
                }

            }
        }, new CompletionListener<Void>() {
               @Override
               public void onSuccess(NonBlockingClient c, Void ctx) {
                   logger.log(Level.INFO, "Subscribed to: "+ queue.value());
               }
               @Override
               public void onError(NonBlockingClient c, Void ctx, Exception exception) {
                   logger.log(Level.INFO, "Error:");
                   exception.printStackTrace();
               }
        }, null);
    }

    @Override
    public void close() {
        client.stop(new CompletionListener<Void>() {
            @Override
            public void onSuccess(NonBlockingClient c, Void ctx) {
                logger.log(Level.INFO, "Client stopped");
            }
            @Override
            public void onError(NonBlockingClient c, Void ctx, Exception exception) {
                logger.log(Level.INFO, "Error");
                exception.printStackTrace();
            }
        }, null);
    }

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
