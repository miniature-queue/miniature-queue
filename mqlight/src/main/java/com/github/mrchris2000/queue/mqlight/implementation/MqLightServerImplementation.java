package com.github.mrchris2000.queue.mqlight.implementation;

import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.QueueType;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.ibm.mqlight.api.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
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
                String decoded = new String(message, StandardCharsets.ISO_8859_1);
                client.send(queue.value(), decoded, null);
            } catch (Exception e) {
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), e);
            }
        } else  {
            try {
                //This is the currently implemented path.
                client.send(queue.value(), ByteBuffer.wrap(message), null);
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
                        ByteBuffer bb = bd.getData();
                        byte[] bytes = bb.array();
                        action.apply(bytes);
                        logger.log(Level.INFO, new String(bytes));
                        break;
                    case STRING:
                        StringDelivery sd = (StringDelivery) delivery;
                        String data = sd.getData();
                        byte[] stringbytes = data.getBytes(StandardCharsets.ISO_8859_1);
                        action.apply(stringbytes);
                        logger.log(Level.INFO, data);
                        break;
                    case JSON:
                        JsonDelivery jd = (JsonDelivery)delivery;
                        action.apply(jd.getRawData().getBytes());
                        logger.log(Level.WARNING, jd.getRawData());

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
                   logger.log(Level.SEVERE, "Error:"+ exception.getMessage());

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
                throw new QueueException("Client currently null", null);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }
}
