package com.github.mlk.queue;

public interface Encoder {
    byte[] encode(Object object) throws CodexException;
    boolean canHandle(Class<?> clazz);
}
