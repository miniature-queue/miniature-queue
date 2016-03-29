package com.github.mlk.queue;

public class QueueException extends  RuntimeException {
    public QueueException(String message, Exception cause) {
        super(message, cause);
    }

}
