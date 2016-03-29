package com.github.mlk.queue.gson;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Decoder;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class GsonDecoder implements Decoder {
    private final Gson gson;
    private final Charset charset;

    public GsonDecoder() {
        this(new Gson());
    }

    public GsonDecoder(Gson gson) {
        this(gson, Charset.forName("UTF-8"));
    }

    public GsonDecoder(Gson gson, Charset charset) {
        this.gson = gson;
        this.charset = charset;
    }

    @Override
    public Object decode(byte[] array, Type type) throws CodexException {
        return gson.fromJson(new String(array, charset), type);
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return true;
    }
}
