package com.github.mlk.queue.implementation;

import com.github.mlk.queue.Queuify;

/** This allows for external modules (such as an encoder/decoder pair) to bind everything in a single call.
 * This should set up with sensible defaults.
 */
public interface Module {

    /** Bind all the modules components to the current builder.
     *
     * @param builder The builder to be set up.
     */
    void bind(Queuify.Builder builder);
}
