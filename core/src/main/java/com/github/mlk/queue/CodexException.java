package com.github.mlk.queue;

public class CodexException extends RuntimeException {
    private final Object actual;

    public CodexException(String message, Exception cause, Object actual) {
        super(message, cause);
        this.actual = actual;
    }

    public Object getActual() {
        return actual;
    }
}
