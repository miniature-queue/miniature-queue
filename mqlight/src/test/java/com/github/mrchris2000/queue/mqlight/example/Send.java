package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mrchris2000.queue.mqlight.MqLightServer;

public class Send {

    public static void main(String... argv) {
        Server server = new MqLightServer("amqp://user:password@localhost");
        MessageQueue mq = Queuify.builder().server(server).target(MessageQueue.class);

        /*for(String s : argv) {
            mq.publishMessage(s);
        }*/
        for(int i=0; i<50; i++){
            mq.publishMessage("Hello MQ Light");
        }

    }
}
