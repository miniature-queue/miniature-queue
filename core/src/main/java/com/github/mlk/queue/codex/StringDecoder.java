package com.github.mlk.queue.codex;

import com.github.mlk.queue.CodexException;
import com.github.mlk.queue.Decoder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/** Can only handle strings in UTF-8. */
public class StringDecoder implements Decoder {
    @Override
    public Object decode(byte[] array, Type type) throws CodexException {
        try {
            return new String(array, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CodexException("UTF-8 does not exist!", e, array);
        }
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return clazz.equals(String.class);
    }
}
