package com.github.mlk.queue;

/** Encodes the given object into a byte array */
public interface Encoder {
    /** Decodes an byte array.
     * Notes for implementors:
     *  - This should throw `CodexException` when it comes across something it can not encode.
     *  - Provide builders for encoders when it makes sense.
     *  - Provide a Module to ease setup.
     *
     * @param object The object
     * @return A byte array representing this object.
     * @throws CodexException Any exceptions processing this request.
     */
    byte[] encode(Object object) throws CodexException;

    /** Can this encoder handle the given class. This is called during proxy construction and will cause the application to fail early.
     *
     * @param clazz The class to check
     * @return true if this encoder can decode the given class.
     */
    boolean canHandle(Class<?> clazz);
}
