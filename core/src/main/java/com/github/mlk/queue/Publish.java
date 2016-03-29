package com.github.mlk.queue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This annotation is applied to methods that will send a message to the queue.
 * The method this is applied to MUST have one parameter and that parameter MUST be encode-able with the current encoder.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Publish {
}
