package com.github.mlk.queue.implementation;

import com.github.mlk.queue.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Function;

public class QueueHandler implements InvocationHandler {
    private final Encoder encoder;
    private final Decoder decoder;
    private final Queue queue;
    private final ServerImplementation implementation;


    public QueueHandler(Encoder encoder, Decoder decoder, Queue queue, ServerImplementation implementation) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.implementation = implementation;
        this.queue = queue;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getAnnotation(Publish.class) != null) {
            byte[] message = encoder.encode(args[args.length - 1]);
            implementation.publish(queue.value(), message);
        } else if(method.getAnnotation(Handle.class) != null) {
            Function<Object, Boolean> func = (Function<Object, Boolean>)args[args.length - 1];
            implementation.listen(queue.value(), (x) -> func.apply(decoder.decode(x)));
        }

        return null;
    }
}
