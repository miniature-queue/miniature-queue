package com.github.mlk.queue.codex;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/** Utilizes Java Serialization. This is the default. */
public class SerializationEncoder implements Encoder {
    @Override
    public byte[] encode(Object object) throws CodexException {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new CodexException("Failed to write object", e, object);
        }

    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
