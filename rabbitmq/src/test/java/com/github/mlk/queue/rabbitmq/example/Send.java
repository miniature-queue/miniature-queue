package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;

public class Send {

    public static void main(String... argv) {
        Server server = new RabbitMqServer("localhost");
        MessageQueue mq = Queuify.builder().server(server).target(MessageQueue.class);
        for(String s : argv) {
            mq.publishMessage(s);
        }
        server.close();
    }
}
