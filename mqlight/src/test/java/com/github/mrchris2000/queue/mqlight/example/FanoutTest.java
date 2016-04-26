package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.mrchris2000.queue.mqlight.MqLightServer;
import com.ibm.mqlight.api.CompletionListener;
import com.ibm.mqlight.api.NonBlockingClient;
import com.ibm.mqlight.api.NonBlockingClientAdapter;
import org.junit.Rule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;

import javax.xml.bind.DatatypeConverter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;

import static org.junit.Assert.assertTrue;

public class FanoutTest {
    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .env("LICENSE", "accept")
                    .imageName("ibmcom/mqlight:1.0")
                    .publishAllPorts(true)
                    .waitForMessage("Starting MQ Light", 120)
                    .build();

    @Queue(value = "/fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        System.out.println("amqp://user:password@" + dockerRule.getDockerHost() + ":" + dockerRule.getExposedContainerPort("5672"));
        System.out.println("Yeay!");
        Thread.sleep(5000L);
        System.out.println("Running test...");
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        MqLightServer mqls =  new MqLightServer("amqp://user:password@" + dockerRule.getDockerHost() + ":" + dockerRule.getExposedContainerPort("5672"));
        MqLightServer mqls2 =  new MqLightServer("amqp://user:password@" + dockerRule.getDockerHost() + ":" + dockerRule.getExposedContainerPort("5672"));
        MqLightServer mqls3 =  new MqLightServer("amqp://user:password@" + dockerRule.getDockerHost() + ":" + dockerRule.getExposedContainerPort("5672"));

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
