package com.github.mlk.queue;

/** Something has gone horribly wrong when either putting something on a queue, to registering a listener. */
public class QueueException extends RuntimeException {
    public QueueException(String message, Exception cause) {
        super(message, cause);
    }

}
