package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.mrchris2000.queue.mqlight.MqLightServer;
import org.junit.Rule;
import org.junit.Test;
import pl.domzal.junit.docker.rule.DockerRule;

import javax.xml.bind.DatatypeConverter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class FanoutTest {
    @Rule
    public DockerRule dockerRule =
            DockerRule.builder()
                    .imageName("ibmcom/mqlight:1.0")
                    .env("LICENSE","accept")
                    .env("volume", "/var/example:/var/mqlight")
                    .env("MQLIGHT_USER","user")
                    .env("MQLIGHT_PASSWORD","password")
                    .publishAllPorts(true)
                    .waitForMessage("Monitoring MQ Light...", 90)
                    .build();

    @Queue(value = "/fanout-example", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test(timeout = 120000)
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        MqLightServer mqls =  new MqLightServer("amqp://user:password@"+ dockerRule.getDockerHost()+":"+dockerRule.getExposedContainerPort("5672"));
        MqLightServer mqls2 =  new MqLightServer("amqp://user:password@"+ dockerRule.getDockerHost()+":"+dockerRule.getExposedContainerPort("5672"));

        FanoutExampleQueue one = Queuify.builder().decoder(new StringDecoder()).server(mqls).target(FanoutExampleQueue.class);
        FanoutExampleQueue two = Queuify.builder().decoder(new StringDecoder()).server(mqls2).target(FanoutExampleQueue.class);
        FanoutExampleQueue sender = Queuify.builder().encoder(new StringEncoder()).server(mqls).target(FanoutExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });

        Thread.sleep(5000L); // Allow time for Docker container to be properly ready (Despite saying it's good to go).
        sender.publishMessage("msg");
        Thread.sleep(500L); // Allow time for message delivery to clients.

        assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());


    }
}
