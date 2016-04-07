package com.github.mlk.queue.jedis.implementation;

import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.QueueType;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.jedis.codex.ToString;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JedisServerImplementation implements ServerImplementation {
    private final Logger log = Logger.getLogger(getClass().getName());

    private final JedisPool pool;
    private JedisPubSub subscriptions;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, Collection<Function<byte[], Boolean>>> actionMap = Collections.synchronizedMap(new HashMap<>());
    private final ThreadFactory threadFactory;
    private final ToString toStringCodex;
    private Thread listenerThread;
    private JedisException exceptionInListenerThread;

    public JedisServerImplementation(JedisPool pool, ThreadFactory threadFactory, ToString toStringCodex) {
        this.pool = pool;
        this.threadFactory = threadFactory;
        this.toStringCodex = toStringCodex;
    }

    @Override
    public void publish(Queue queue, byte[] message) throws QueueException {
        validateQueue(queue);

        try (Jedis jedis = pool.getResource()) {
            jedis.publish(queue.value(), toStringCodex.encode(message));
        } catch(JedisException e) {
            throw new QueueException(e.getMessage(), e);
        }
    }

    private void validateQueue(Queue queue) {
        if(!queue.queueTypeHint().equals(QueueType.FANOUT_QUEUE)) {
            log.warning("Queue: " + queue.value() + " is not a FANOUT_QUEUE");
        }
    }

    @Override
    public void listen(Queue queue, Function<byte[], Boolean> action) throws QueueException {
        validateQueue(queue);

        lock.lock();
        try {
            addAction(queue, action);

            if (subscriptions == null) {
                createSubscriptionListener();

                listenerThread = threadFactory.newThread(() -> {
                    try (Jedis jedis = pool.getResource()) {
                        jedis.subscribe(subscriptions, queue.value());
                    } catch(JedisException e) {
                        exceptionInListenerThread = e;
                    }
                });
                listenerThread.start();
                // Wait for Jedis...
                while(!subscriptions.isSubscribed() && listenerThread.isAlive()) { Thread.yield(); }
                if(exceptionInListenerThread != null) {
                    throw new QueueException(exceptionInListenerThread.getMessage(), exceptionInListenerThread);
                }
            } else {
                if(exceptionInListenerThread != null) {
                    throw new QueueException(exceptionInListenerThread.getMessage(), exceptionInListenerThread);
                }
                subscriptions.subscribe(queue.value());
            }
        } finally {
            lock.unlock();
        }
    }

    private void createSubscriptionListener() {
        subscriptions = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Collection<Function<byte[], Boolean>> currentActions = actionMap.get(channel);
                if(currentActions != null) {
                    for(Function<byte[], Boolean> currentAction : currentActions) {
                        if(!currentAction.apply(toStringCodex.decode(message))) {
                            log.warning("attempt to not ack " + channel + " however this is not supported");
                        }
                    }
                }

            }
        };
    }

    private void addAction(Queue queue, Function<byte[], Boolean> action) {
        Collection<Function<byte[], Boolean>> actions = actionMap.get(queue.value());
        if(actions == null) {
            actions = new ArrayList<>();
            actionMap.put(queue.value(), actions);
        }
        actions.add(action);
    }

    @Override
    public void close() {
        if(subscriptions != null) {
            try {
                subscriptions.unsubscribe();
            } catch(JedisException e) {
                log.log(Level.FINE, "Exception when unsubscribing", e);
            }

            try {
                listenerThread.join();
            } catch (InterruptedException e) {
                log.log(Level.FINE, "Exception waiting for listener thread to close", e);
            }
        }
        try {
            pool.destroy();
        } catch(JedisException e) {
            log.log(Level.FINE, "Exception when closing pool", e);
        }
    }
}
