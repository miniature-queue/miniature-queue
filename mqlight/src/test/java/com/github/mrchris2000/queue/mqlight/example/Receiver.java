package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mrchris2000.queue.mqlight.MqLightServer;

public class Receiver {
    public static void main(String... argv) {
        Server server = new MqLightServer("amqp://user:password@localhost:5672");
        MessageQueue mq = Queuify.builder().server(server).target(MessageQueue.class);

        mq.receiveMessage((x) -> {
            System.out.println(x);
            return true;
        });
        System.out.println("Waiting for messages...");

    }
}
