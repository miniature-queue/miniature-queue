package com.github.mlk.queue;

/** Something has gone horribly wrong when attempting to encode or decode a value. */
public class CodexException extends RuntimeException {
    private final Object actual;

    public CodexException(String message, Exception cause, Object actual) {
        super(message, cause);
        this.actual = actual;
    }

    /**  @return The value that could not be encoded or decoded. */
    public Object getActual() {
        return actual;
    }
}
