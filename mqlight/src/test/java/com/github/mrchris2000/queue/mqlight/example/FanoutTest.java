package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.mrchris2000.queue.mqlight.MqLightServer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class FanoutTest {
/*    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .env("LICENSE", "accept")
                    .imageName("ibmcom/mqlight:1.0")
                    .publishAllPorts(true)
                    .waitForMessage("MQ Light Server ready to use at", 200)
                    .keepContainer(false)
                    .expose("5672", "5672/tcp")
                    .build();*/

    @Queue(value = "/fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        String connectionString = "amqp://user:password@192.168.99.100"; //+ dockerRule.getDockerHost();// + ":" + dockerRule.getExposedContainerPort("5672");
        System.out.println(connectionString);
        System.out.println("Running test...");
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        MqLightServer mqls =  new MqLightServer(connectionString);
        MqLightServer mqls2 =  new MqLightServer(connectionString);
        MqLightServer mqls3 =  new MqLightServer(connectionString);

        FanoutExampleQueue one = Queuify.builder().decoder(new StringDecoder()).server(mqls).target(FanoutExampleQueue.class);
        FanoutExampleQueue two = Queuify.builder().decoder(new StringDecoder()).server(mqls2).target(FanoutExampleQueue.class);
        FanoutExampleQueue sender = Queuify.builder().encoder(new StringEncoder()).server(mqls3).target(FanoutExampleQueue.class);

        one.receiveMessage((x) -> { System.out.println("one"); oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { System.out.println("two"); twoReceiveMessage.set(true); return true; });

        Thread.sleep(10000L);

        sender.publishMessage("msg");
        Thread.sleep(5000L);

        assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());
    }
}
