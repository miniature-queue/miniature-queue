package com.github.mlk.queue.gson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Encoder;

public class JacksonEncoder implements Encoder {
    private final ObjectMapper objectMapper;

    public JacksonEncoder() {
        this(new ObjectMapper());
    }

    public JacksonEncoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] encode(Object object) throws CodexException {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new CodexException("Failed to write object", e, object);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
