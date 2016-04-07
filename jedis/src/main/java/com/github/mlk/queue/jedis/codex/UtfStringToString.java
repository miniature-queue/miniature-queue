package com.github.mlk.queue.jedis.codex;

import java.nio.charset.Charset;

/** This assumes that the byte array is already a Java string, for example when using a JSON encoding. */
public class UtfStringToString implements ToString {
    @Override
    public byte[] decode(String value) {
        return value.getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public String encode(byte[] value) {
        return new String(value, Charset.forName("UTF-8"));
    }
}
