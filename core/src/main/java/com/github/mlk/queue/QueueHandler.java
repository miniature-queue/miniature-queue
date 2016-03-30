package com.github.mlk.queue;

import com.github.mlk.queue.implementation.ServerImplementation;
import com.google.common.base.Function;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getAnnotation(Publish.class) != null) {
            byte[] message = encoder.encode(args[args.length - 1]);
            implementation.publish(queue.value(), message);
        } else if(method.getAnnotation(Handle.class) != null) {
            final Function<Object, Boolean> func = (Function<Object, Boolean>)args[args.length - 1];
            final ParameterizedType type = (ParameterizedType)method.getGenericParameterTypes()[args.length - 1];
            implementation.listen(queue.value(), new Function<byte[], Boolean>() {

                @Override
                public Boolean apply(byte[] x) {
                    return func.apply(decoder.decode(x, type.getActualTypeArguments()[0]));
                }
            } );
        }

        return null;
    }
}
