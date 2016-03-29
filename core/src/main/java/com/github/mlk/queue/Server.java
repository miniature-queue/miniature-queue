package com.github.mlk.queue;

import com.github.mlk.queue.implementation.ServerImplementation;

/** This represents an MQ server. Each MQ API should provide one or more of these this represents the MQ server.
 *
 */
public abstract class Server implements AutoCloseable {
    /** MQ APIs should implement this method. It should return a single ServerImplementation per Server.
     *
     * @return A server implementation.
     */
    protected abstract ServerImplementation getImplementation();

    /** Closes the connection to this server. */
    @Override
    public void close() {
        getImplementation().close();
    }
}
