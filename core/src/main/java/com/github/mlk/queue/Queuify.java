package com.github.mlk.queue;

import com.github.mlk.queue.codex.SerializationDecoder;
import com.github.mlk.queue.codex.SerializationEncoder;
import com.google.common.base.Function;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/** The entry point to miniature-queue. Set up the builder then use `target` to register queues.
 *
 */
public class Queuify {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Encoder encoder = new SerializationEncoder();
        private Decoder decoder = new SerializationDecoder();
        private Server server;

        private Builder() { }

        public Builder encoder(Encoder encoder) {
            verifyNotNull(encoder);
            this.encoder = encoder;
            return this;
        }

        public Builder decoder(Decoder decoder) {
            verifyNotNull(decoder);
            this.decoder = decoder;
            return this;
        }
        public Builder server(Server server) {
            verifyNotNull(server);
            this.server = server;
            return this;
        }

        /** Creates a queue based on the current configuration of the builder.
         *
         * @param clazz The target queue.
         * @param <T> The target queue.
         * @return An instance of the queue.
         */
        public <T> T target(Class<T> clazz) {
            verifyNotNull(clazz);
            verifyIsInterface(clazz);
            verifyAllMethodsAreLinkedToAction(clazz);
            verifyAllPublishMethodsAreValid(clazz);
            verifyAllHandleMethodsAreValid(clazz);
            verifyHasServer();
            Queue queue = verifyHasQueueAnnotation(clazz);

            return clazz.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz}, new QueueHandler(encoder, decoder, queue, server.getImplementation())));
        }

        private <T> void verifyAllHandleMethodsAreValid(Class<T> clazz) {
            for (Method method : clazz.getMethods()) {

                if (method.getAnnotation(Handle.class) != null) {
                    if (!method.getReturnType().equals(void.class)) {
                        throw new IllegalArgumentException(method.getName() + " does not return void");
                    }

                    Class[] paramClasses = method.getParameterTypes();
                    if (paramClasses.length != 1) {
                        throw new IllegalArgumentException(method.getName() + " must have one param");
                    }

                    if (!paramClasses[paramClasses.length - 1].equals(Function.class)) {
                        throw new IllegalArgumentException(method.getName() + " parameter must be a Function");
                    } else {
                        ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[paramClasses.length - 1];
                        Type returnType = type.getActualTypeArguments()[1];

                        if(!returnType.equals(Boolean.class)) {
                            throw new IllegalArgumentException(method.getName() + " function must return a boolean");
                        }

                        Type inputType = type.getActualTypeArguments()[0];
                        if(!decoder.canHandle((Class<?>) inputType)) {
                            throw new IllegalArgumentException(method.getName() + " function must take an object the decoder can process");
                        }

                    }

                }
            }
        }

        private <T> void verifyAllPublishMethodsAreValid(Class<T> clazz) {
            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(Publish.class) != null) {
                    if(!method.getReturnType().equals(void.class)) {
                        throw new IllegalArgumentException(method.getName() + " does not return void");
                    }

                    Class[] paramClasses = method.getParameterTypes();
                    if(paramClasses.length != 1) {
                        throw new IllegalArgumentException(method.getName() + " must have one param");
                    }

                    if (!encoder.canHandle(paramClasses[paramClasses.length - 1])) {
                        throw new IllegalArgumentException(method.getName() + " passes type not supported by encoder");
                    }

                }
            }
        }

        private <T> void verifyAllMethodsAreLinkedToAction(Class<T> clazz) {
            Class<?>[] actionAnnotations = new Class<?>[] { Publish.class, Handle.class };

            for(Method method : clazz.getMethods()) {
                boolean foundAnnotation = false;
                for(Class annotation : actionAnnotations) {
                    if(method.getAnnotation(annotation) != null) {
                        foundAnnotation = true;
                        break;
                    }
                }
                if(!foundAnnotation) {
                    throw new IllegalArgumentException(method.getName() + " does not have an action annotation");
                }
            }
        }

        private <T> void verifyIsInterface(Class<T> clazz) {
            if(!clazz.isInterface()) {
                throw new IllegalArgumentException(clazz + " not an interface");
            }
        }

        private void verifyHasServer() {
            if(server == null) {
                throw new IllegalStateException("server == null");
            }
        }

        private Queue verifyHasQueueAnnotation(Class<?> clazz) {
            Queue q = clazz.getAnnotation(Queue.class);
            if(q == null) {
                throw new IllegalArgumentException("no queue annotation");
            }
            return q;
        }

        private void verifyNotNull(Object object) {
            if(object == null) {
                throw new IllegalArgumentException("param is null");
            }
        }
    }

}
