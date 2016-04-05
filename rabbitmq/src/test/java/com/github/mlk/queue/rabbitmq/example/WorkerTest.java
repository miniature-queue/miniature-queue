package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkerTest {
    @Queue(value = "worker-example", queueTypeHint = QueueType.WORKER_QUEUE)
    interface WorkerExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);
        WorkerExampleQueue one = Queuify.builder().server(new RabbitMqServer("localhost")).target(WorkerExampleQueue.class);
        WorkerExampleQueue two = Queuify.builder().server(new RabbitMqServer("localhost")).target(WorkerExampleQueue.class);
        WorkerExampleQueue sender = Queuify.builder().server(new RabbitMqServer("localhost")).target(WorkerExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });

        sender.publishMessage("msg");
        Thread.sleep(100L);

        assertFalse(oneReceiveMessage.get() && twoReceiveMessage.get());
        assertTrue(oneReceiveMessage.get() || twoReceiveMessage.get());
    }
}
