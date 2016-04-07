package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.jedis.JedisServer;

public class Receiver {
    public static void main(String... argv) {
        MessageQueue mq = Queuify.builder().server(new JedisServer("localhost")).target(MessageQueue.class);
        mq.receiveMessage((x) -> { System.out.println(x); return true; } );
        System.out.println("Waiting for messages...");
    }
}
