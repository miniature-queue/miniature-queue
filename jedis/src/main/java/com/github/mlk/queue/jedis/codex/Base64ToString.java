package com.github.mlk.queue.jedis.codex;

import java.util.Base64;

/** Base64 string */
public class Base64ToString implements ToString {
    @Override
    public byte[] decode(String value) {
        return Base64.getDecoder().decode(value);
    }

    @Override
    public String encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }
}
