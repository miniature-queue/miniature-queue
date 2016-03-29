package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;

public class Send {

    public static void main(String... argv) {
        MessageQueue mq = Queuify.builder().server(new RabbitMqServer("localhost")).target(MessageQueue.class);
        for(String s : argv) {
            mq.publishMessage(s);
        }
    }
}
