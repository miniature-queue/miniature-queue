package com.github.mlk.queue.codex;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Encoder;

import java.io.UnsupportedEncodingException;

/** Can only handle strings in UTF-8. */
public class StringEncoder implements Encoder {
    @Override
    public byte[] encode(Object object) throws CodexException {
        try {
            return object.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CodexException("UTF-8 does not exist!", e, object);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return clazz.equals(String.class);
    }
}
