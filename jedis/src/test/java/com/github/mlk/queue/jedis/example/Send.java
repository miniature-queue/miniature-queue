package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mlk.queue.jedis.JedisServer;

public class Send {

    public static void main(String... argv) {
        Server server = new JedisServer("localhost");
        MessageQueue mq = Queuify.builder().server(server).target(MessageQueue.class);
        for(String s : argv) {
            mq.publishMessage(s);
        }
        server.close();
    }
}
