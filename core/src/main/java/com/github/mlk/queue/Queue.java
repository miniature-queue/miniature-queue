package com.github.mlk.queue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Applied to an interface to specify that said interface is the given MQ queue. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Queue {
    /** @return The name of the queue. */
    String value();
}
