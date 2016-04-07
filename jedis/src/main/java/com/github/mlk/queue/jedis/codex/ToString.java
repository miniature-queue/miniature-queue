package com.github.mlk.queue.jedis.codex;

/** Encodes & decodes byte arrays to Strings. */
public interface ToString {
    /** Converts a string from RADIS into a byte array.
     *
     * @param value The String to convert.
     * @return The value represented as a byte array.
     */
    byte[] decode(String value);

    /** Converts a byte array from the application to a string for RADIS
     *
     * @param value The data to convert.
     * @return The value represented as a string.
     */
    String encode(byte[] value);
}
