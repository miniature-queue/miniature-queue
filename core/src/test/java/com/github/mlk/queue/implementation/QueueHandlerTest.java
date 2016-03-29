package com.github.mlk.queue.implementation;

import com.github.mlk.queue.*;
import com.github.mlk.queue.implementation.codex.StringDecoder;
import com.github.mlk.queue.implementation.codex.StringEncoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class QueueHandlerTest {

    @Test
    public void whenMethodHasPublishThenCallTheQueueName() {
        Object param = new Object();
        byte[] paramAsArray = new byte[0];

        Encoder encoder = mock(Encoder.class);
        Decoder decoder = mock(Decoder.class);
        when(encoder.encode(param)).thenReturn(paramAsArray);

        ServerImplementation implementation = mock(ServerImplementation.class);

        PublishTestObject subjectProxy = (PublishTestObject) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {PublishTestObject.class},
                new QueueHandler(encoder, decoder, new QueueImp("queueName") , implementation) );

        subjectProxy.queue(param);

        verify(implementation).publish("queueName", paramAsArray);
    }

    @Test
    public void whenMethodHasHandleThenCallTheQueueName() throws UnsupportedEncodingException {
        List<String> actions = new ArrayList<>();

        Encoder encoder = new StringEncoder();
        Decoder decoder = new StringDecoder();

        RecordingServerImplementation implementation = new RecordingServerImplementation();

        HandleTestObject subjectProxy = (HandleTestObject) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {HandleTestObject.class},
                new QueueHandler(encoder, decoder, new QueueImp("queueName"), implementation) );

        subjectProxy.consume((x) -> { actions.add(x); return true; });

        implementation.action.apply("Hello".getBytes("UTF-8"));

        assertThat(actions.get(0), is("Hello"));
    }

    static class QueueImp implements Queue {

        private final String queueName;

        QueueImp(String queueName) {
            this.queueName = queueName;
        }

        @Override
        public String value() {
            return queueName;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Queue.class;
        }
    }
}

@Queue("queueName")
interface PublishTestObject {
    @Publish
    void queue(Object o);
}

@Queue("queueName")
interface HandleTestObject {
    @Handle
    void consume(Function<String, Boolean> handler);
}

class RecordingServerImplementation implements ServerImplementation {

    String queue;
    Function<byte[], Boolean> action;

    @Override
    public void publish(String queueName, byte[] message) {

    }

    @Override
    public void listen(String queue, Function<byte[], Boolean> action) {
        this.queue = queue;
        this.action = action;
    }
}