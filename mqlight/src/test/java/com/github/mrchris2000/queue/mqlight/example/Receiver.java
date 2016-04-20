package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.mrchris2000.queue.mqlight.MqLightServer;

public class Receiver {
    public static void main(String... argv) {
        Server server = new MqLightServer("amqp://user:password@localhost");
        MessageQueue mq = Queuify.builder().server(server).decoder(new StringDecoder()).target(MessageQueue.class);

        mq.receiveMessage((x) -> {
            System.out.println(x);
            return true;
        });
        System.out.println("Waiting for messages...");

    }
}
