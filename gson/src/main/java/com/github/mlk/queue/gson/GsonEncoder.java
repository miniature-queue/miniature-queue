package com.github.mlk.queue.gson;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Encoder;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.nio.charset.Charset;

public class GsonEncoder implements Encoder {
    private final Gson gson;
    private final Charset charset;

    public GsonEncoder() {
        this(new Gson());
    }

    public GsonEncoder(Gson gson) {
        this(gson, Charset.forName("UTF-8"));
    }

    public GsonEncoder(Gson gson, Charset charset) {
        this.gson = gson;
        this.charset = charset;
    }

    @Override
    public byte[] encode(Object object) throws CodexException {
        try {
            return gson.toJson(object).getBytes(charset);
        } catch(UnsupportedOperationException | JsonParseException e) {
            throw new CodexException(e.getMessage(), e, object);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
