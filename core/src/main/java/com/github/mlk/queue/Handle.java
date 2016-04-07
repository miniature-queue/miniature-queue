package com.github.mlk.queue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This annotation is applied to methods that will register a listener to a queue.
 * The listener must be of type `Function&lt;T, Boolean&gt;`. The Boolean return type signals if the message was
 * correctly consumed.
 * T can be any type the registered decoder can handle.
 *
 * The method will return immediately. The listening thread pool (i.e. what calls the function) is implementation
 * dependent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handle {
}
