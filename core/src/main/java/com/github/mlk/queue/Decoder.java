package com.github.mlk.queue;

import java.lang.reflect.Type;

/** Decodes a byte array into the Type given. */
public interface Decoder {
    /** Decodes an byte array.
     * Notes for implementors:
     *  - This should throw `CodexException` when it comes across something it can not decode.
     *  - Provide builders for encoders when it makes sense.
     *
     * @param array The raw object
     * @param type The expected type returned
     * @return An instance of type `type`.
     * @throws CodexException Any exceptions processing this request.
     */
    Object decode(byte[] array, Type type) throws CodexException;

    /** Can this decoder handle the given class. This is called during proxy construction and will cause the application to fail early.
     *
     * @param clazz The class ti check
     * @return true if this decode can decode the given class.
     */
    boolean canHandle(Class<?> clazz);
}
