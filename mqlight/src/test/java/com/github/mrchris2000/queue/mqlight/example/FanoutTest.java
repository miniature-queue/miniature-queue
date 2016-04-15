package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.*;
import com.github.mrchris2000.queue.mqlight.MqLightServer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class FanoutTest {
    @Queue(value = "/fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        MqLightServer mqls =  new MqLightServer("amqp://user:password@localhost");
        MqLightServer mqls2 =  new MqLightServer("amqp://user:password@localhost");
        FanoutExampleQueue one = Queuify.builder().server(mqls).target(FanoutExampleQueue.class);
        FanoutExampleQueue two = Queuify.builder().server(mqls2).target(FanoutExampleQueue.class);
        FanoutExampleQueue sender = Queuify.builder().server(mqls).target(FanoutExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });

        Thread.sleep(5000L);

        sender.publishMessage("msg");
        Thread.sleep(500L);

        assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());
    }
}
