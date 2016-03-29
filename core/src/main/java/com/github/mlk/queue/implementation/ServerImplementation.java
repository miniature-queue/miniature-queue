package com.github.mlk.queue.implementation;

import java.util.function.Function;

public interface ServerImplementation {
    void publish(String queueName, byte[] message);
    void listen(String queue, Function<byte[], Boolean> action);
}
