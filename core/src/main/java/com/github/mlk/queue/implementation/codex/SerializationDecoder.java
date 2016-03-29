package com.github.mlk.queue.implementation.codex;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SerializationDecoder implements Decoder {
    @Override
    public Object decode(byte[] array) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CodexException("Failed to read object", e, array);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
