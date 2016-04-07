package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.jedis.JedisServer;
import org.junit.Test;

public class ExceptionsTest {
    @Test(expected = QueueException.class)
    public void whenExceptionDuringListenThenExceptionWrappedAndThrown() {
        JedisServer server = new JedisServer("idontexist.example.invalid");
        try {
            Queuify.builder().server(server).target(FanoutTest.FanoutExampleQueue.class).receiveMessage((x) -> true);
        } finally {
            server.close();
        }
    }
}
