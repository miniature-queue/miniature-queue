package com.github.mlk.queue.codex;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

/** Utilizes Java Serialization. This is the default. */
public class SerializationDecoder implements Decoder {
    @Override
    public Object decode(byte[] array, Type type) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CodexException("Failed to read object", e, array);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        if(clazz == null) {
            return false;
        }

        if(clazz.equals(Serializable.class)) {
            return true;
        }

        for (Class<?> cur : clazz.getInterfaces()) {
            if(canHandle(cur)) {
                return true;
            }
        }

        return (canHandle(clazz.getSuperclass()));
    }
}
