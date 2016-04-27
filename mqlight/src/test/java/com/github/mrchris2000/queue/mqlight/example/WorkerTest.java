package com.github.mrchris2000.queue.mqlight.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.mrchris2000.queue.mqlight.MqLightServer;
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
                    .imageName("ibmcom/mqlight:1.0")
                    .env("LICENSE","accept")
                    .env("MQLIGHT_USER","user")
                    .env("MQLIGHT_PASSWORD","password")
                    .publishAllPorts(true)
                    .waitForMessage("Running in standalone mode")
                    .build();

    @Queue(value = "/worker-example", queueTypeHint = QueueType.WORKER_QUEUE)
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

        MqLightServer mqls =  new MqLightServer("amqp://user:password@"+ dockerRule.getDockerHost());
        MqLightServer mqls2 =  new MqLightServer("amqp://user:password@"+ dockerRule.getDockerHost());

        WorkerExampleQueue one = Queuify.builder().decoder(new StringDecoder()).server(mqls).target(WorkerExampleQueue.class);
        WorkerExampleQueue two = Queuify.builder().decoder(new StringDecoder()).server(mqls2).target(WorkerExampleQueue.class);

        WorkerExampleQueue sender = Queuify.builder().encoder(new StringEncoder()).server(mqls).target(WorkerExampleQueue.class);

        one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
        two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });


        sender.publishMessage("msg");
        Thread.sleep(500L);

        assertFalse(oneReceiveMessage.get() && twoReceiveMessage.get());
        assertTrue(oneReceiveMessage.get() || twoReceiveMessage.get());
    }
}
