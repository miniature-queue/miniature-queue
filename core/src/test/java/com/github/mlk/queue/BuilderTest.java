package com.github.mlk.queue;

import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.function.Function;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuilderTest {
    @Test(expected = IllegalArgumentException.class)
    public void cannotSetEncoderToNull() {
        Queuify.builder().encoder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetDecoderToNull() {
        Queuify.builder().decoder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetServerToNull() {
        Queuify.builder().server(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestNullTarget() {
        Queuify.builder().server(mock(Server.class)).target(null);
    }

    @Test(expected = IllegalStateException.class)
    public void cannotCreateTargetWithoutServer() {
        Queuify.builder().target(PublishOk.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestTargetWithoutQueueAnnotation() {
        Queuify.builder().server(mock(Server.class)).target(PublishHasNoQueue.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestTargetThatIsNotInterface() {
        Queuify.builder().server(mock(Server.class)).target(PublishClass.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestTargetThatTheDecoderCanNotDecode() {
        Encoder encoder = mock(Encoder.class);
        when(encoder.canHandle(String.class)).thenReturn(false);

        Queuify.builder().server(mock(Server.class)).encoder(encoder).target(PublishOk.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestTargetThatHasNotActions() {
        Queuify.builder().server(mock(Server.class)).target(PublishNoActionAnnotation.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestNoneVoidAction() {
        Queuify.builder().server(mock(Server.class)).target(PublishNotVoid.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotRequestTargetWithNoParams() {
        Queuify.builder().server(mock(Server.class)).target(PublishNoParam.class);
    }

    @Test
    public void onValidInterfaceThenReturnProxy() {
        PublishOk publishOk = Queuify.builder().server(mock(Server.class)).target(PublishOk.class);
        assertThat(Proxy.getInvocationHandler(publishOk), instanceOf(QueueHandler.class));
    }

    @Test
    public void handle_onValidInterfaceThenReturnProxy() {
        HandleOk publishOk = Queuify.builder().server(mock(Server.class)).target(HandleOk.class);
        assertThat(Proxy.getInvocationHandler(publishOk), instanceOf(QueueHandler.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleMustTakeFunction() {
        Queuify.builder().server(mock(Server.class)).target(HandleDoesNotTakeFunction.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleMustTakeFunctionThatReturnsBoolean() {
        Queuify.builder().server(mock(Server.class)).target(HandleDoesNotReturnBoolean.class);
    }
}

@Queue("the_queue")
interface HandleDoesNotTakeFunction {
    @Handle
    void consume(String newMessage);
}

@Queue("the_queue")
interface HandleOk {
    @Handle
    void consume(Function<String, Boolean> newMessage);
}

@Queue("the_queue")
interface HandleDoesNotReturnBoolean {
    @Handle
    void consume(Function<String, String> newMessage);
}

@Queue("the_queue")
interface PublishOk {
    @Publish
    void publish(String message);
}

@Queue("the_queue")
interface PublishNoParam {
    @Publish
    void publish();
}

@Queue("the_queue")
interface PublishNotVoid {
    @Publish
    boolean publish(String message);
}


@Queue("the_queue")
interface PublishNoActionAnnotation {
    void publish(String message);
}

@Queue("the_queue")
class PublishClass {
    @Publish
    void publish(String message) {}
}

interface PublishHasNoQueue {
    @Publish
    void publish(String message);
}