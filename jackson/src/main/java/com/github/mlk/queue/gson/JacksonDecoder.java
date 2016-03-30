package com.github.mlk.queue.gson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonDecoder implements Decoder {
    private final ObjectMapper objectMapper;

    public JacksonDecoder() {
        this(new ObjectMapper());
    }

    public JacksonDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(byte[] array, Type type) throws CodexException {

        try {
            return objectMapper.readValue(array, objectMapper.constructType(type));
        } catch (IOException e) {
            throw new CodexException("Failed to read object", e, array);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
