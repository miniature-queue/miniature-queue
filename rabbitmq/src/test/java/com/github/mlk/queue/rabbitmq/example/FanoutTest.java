package com.github.mlk.queue.rabbitmq.example;

import com.github.geowarin.junit.DockerRule;
import com.github.mlk.queue.*;
import com.github.mlk.queue.rabbitmq.RabbitMqServer;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class FanoutTest {

    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .image("rabbitmq:latest")
                    .ports("5672/tcp")
                    .waitForPort("5672/tcp")
                    .build();

    @Queue(value = "fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    ConnectionFactory create() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(dockerRule.getDockerHost());
        factory.setPort(dockerRule.getHostPort("5672/tcp"));
        return factory;
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);
        FanoutExampleQueue one = Queuify.builder().server(new RabbitMqServer(create())).target(FanoutExampleQueue.class);
        FanoutExampleQueue two = Queuify.builder().server(new RabbitMqServer(create())).target(FanoutExampleQueue.class);
        FanoutExampleQueue sender = Queuify.builder().server(new RabbitMqServer(create())).target(FanoutExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });

        sender.publishMessage("msg");
        Thread.sleep(100L);

        assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());
    }
}
