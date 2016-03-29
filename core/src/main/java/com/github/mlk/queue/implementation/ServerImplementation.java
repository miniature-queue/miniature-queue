package com.github.mlk.queue.implementation;

import com.github.mlk.queue.QueueException;

import java.util.function.Function;

public interface ServerImplementation {
    void publish(String queueName, byte[] message) throws QueueException;
    void listen(String queue, Function<byte[], Boolean> action) throws QueueException;
    void close();
}
