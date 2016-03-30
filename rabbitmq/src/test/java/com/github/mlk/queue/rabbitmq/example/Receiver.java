package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;
import com.google.common.base.Function;

public class Receiver {
    public static void main(String... argv) {
        MessageQueue mq = Queuify.builder().server(new RabbitMqServer("localhost")).target(MessageQueue.class);
        mq.receiveMessage(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String x) {
                System.out.println(x);
                return true;
            }

        } );
        System.out.println("Waiting for messages...");
    }
}
