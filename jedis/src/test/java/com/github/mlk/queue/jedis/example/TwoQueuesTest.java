package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.jedis.JedisServer;
import org.junit.Rule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TwoQueuesTest {

    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .imageName("redis:latest")
                    .expose("6379", "6379/tcp")
                    .build();

    @Queue(value = "queue1", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface QueueOne {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Queue(value = "queue2", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface QueueTwo {
        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenMessageSentToOneQueueOnlyThatQueueRecievesMessage() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);
        JedisServer server = new JedisServer(new JedisPool(dockerRule.getDockerHost(), 6379));

        Queuify.Builder builder = Queuify.builder().server(server);
        QueueOne queueOne = builder.target(QueueOne.class);
        QueueTwo queueTwo = builder.target(QueueTwo.class);

        queueOne.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; } );
        queueTwo.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; } );

        // Give REDIS some time to get ready...
        Thread.sleep(500);

        queueOne.publishMessage("");

        // Give REDIS some time to send the message
        Thread.sleep(500);

        assertTrue(oneReceiveMessage.get());
        assertFalse(twoReceiveMessage.get());

    }
}
