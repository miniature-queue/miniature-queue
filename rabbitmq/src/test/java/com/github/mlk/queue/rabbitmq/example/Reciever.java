package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;

public class Reciever {
    public static void main(String... argv) {
        MessageQueue mq = Queuify.builder().server(new RabbitMqServer("localhost")).target(MessageQueue.class);
        mq.receiveMessage((x) -> { System.out.println(x); return true; } );
    }
}
