package com.github.mlk.queue;

public interface Decoder {
    Object decode(byte[] array) throws CodexException;
    boolean canHandle(Class<?> clazz);
}
