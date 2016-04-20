package com.github.mlk.queue.rabbitmq.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Rule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkerTest {
    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .imageName("rabbitmq:latest")
                    .expose("5672", "5672/tcp")
                    .build();

    @Queue(value = "worker-example", queueTypeHint = QueueType.WORKER_QUEUE)
    interface WorkerExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }


    ConnectionFactory create() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(dockerRule.getDockerHost());
        factory.setPort(5672);
        return factory;
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        Thread.sleep(5000L);

        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);
        WorkerExampleQueue one = Queuify.builder().server(new RabbitMqServer(create())).target(WorkerExampleQueue.class);
        WorkerExampleQueue two = Queuify.builder().server(new RabbitMqServer(create())).target(WorkerExampleQueue.class);
        WorkerExampleQueue sender = Queuify.builder().server(new RabbitMqServer(create())).target(WorkerExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });

        sender.publishMessage("msg");
        Thread.sleep(100L);

        assertFalse(oneReceiveMessage.get() && twoReceiveMessage.get());
        assertTrue(oneReceiveMessage.get() || twoReceiveMessage.get());
    }
}
