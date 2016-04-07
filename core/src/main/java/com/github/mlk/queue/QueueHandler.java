package com.github.mlk.queue;

import com.github.mlk.queue.implementation.ServerImplementation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.function.Consumer;
import java.util.function.Function;

class QueueHandler implements InvocationHandler {
    private final Encoder encoder;
    private final Decoder decoder;
    private final Queue queue;
    private final ServerImplementation implementation;

    QueueHandler(Encoder encoder, Decoder decoder, Queue queue, ServerImplementation implementation) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.implementation = implementation;
        this.queue = queue;
    }

    @SuppressWarnings("all")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getAnnotation(Publish.class) != null) {
            byte[] message = encoder.encode(args[args.length - 1]);
            implementation.publish(queue, message);
        } else if(method.getAnnotation(Handle.class) != null) {
            Object functionCall = args[args.length - 1];
            if(functionCall instanceof  Function) {
                Function<Object, Boolean> func = (Function<Object, Boolean>) functionCall;
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[args.length - 1];
                implementation.listen(queue, (x) -> func.apply(decoder.decode(x, type.getActualTypeArguments()[0])));
            } else if (functionCall instanceof Consumer) {
                Consumer<Object> func = (Consumer<Object>) functionCall;
                ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[args.length - 1];
                implementation.listen(queue, (x) -> { func.accept(decoder.decode(x, type.getActualTypeArguments()[0])); return true;});
            }
        }

        return null;
    }
}
