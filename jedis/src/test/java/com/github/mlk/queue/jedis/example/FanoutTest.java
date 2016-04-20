package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.jedis.JedisServer;
import org.junit.Rule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class FanoutTest {
    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .imageName("redis:latest")
                    .publishAllPorts(true)
                    .waitForMessage("Running in standalone mode")
                    .build();

    @Queue(value = "fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
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

        JedisServer s1 = new JedisServer(new JedisPool(dockerRule.getDockerHost(), Integer.parseInt(dockerRule.getExposedContainerPort("6379"))));
        JedisServer s2 = new JedisServer(new JedisPool(dockerRule.getDockerHost(), Integer.parseInt(dockerRule.getExposedContainerPort("6379"))));
        JedisServer s3 = new JedisServer(new JedisPool(dockerRule.getDockerHost(), Integer.parseInt(dockerRule.getExposedContainerPort("6379"))));

        try {
            FanoutExampleQueue sender = Queuify.builder().server(s3).target(FanoutExampleQueue.class);

            FanoutExampleQueue one = Queuify.builder().server(s1).target(FanoutExampleQueue.class);
            one.receiveMessage((x) -> {
                oneReceiveMessage.set(true);
                return true;
            });


            FanoutExampleQueue two = Queuify.builder().server(s2).target(FanoutExampleQueue.class);
            two.receiveMessage((x) -> {
                twoReceiveMessage.set(true);
                return true;
            });

            // Give REDIS some time to get ready...
            Thread.sleep(500);

            sender.publishMessage("msg");

            // Give REDIS some time to send the message
            Thread.sleep(500);

            assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());
        } finally {
            s1.close();
            s2.close();
            s3.close();
        }
    }
}
