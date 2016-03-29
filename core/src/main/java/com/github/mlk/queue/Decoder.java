package com.github.mlk.queue;

import java.lang.reflect.Type;

public interface Decoder {
    Object decode(byte[] array, Type type) throws CodexException;
    boolean canHandle(Class<?> clazz);
}
