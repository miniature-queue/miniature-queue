package com.github.mlk.queue.implementation;

import com.github.mlk.queue.QueueException;

import java.util.function.Function;

/** This should be implemented by MQ Clients.
 *
 * Notes:
 *  - Implementations should be thread safe. If they are not this MUST be clearly noted in the documentation.
 *  - Implementations should try and create the queue if it does not exist, where it can not this should be clearly noted in the documentation.
 *  - Implementations should utilize some form of connection pooling. Options for this may be exposed via the `Server`.
 *  - Exceptions should be wrapped with QueueException.
 *  - Implementations should not handle retries here.
 */
public interface ServerImplementation {

    /** Publishes a message to the given queue.
     *
     * @param queueName The queue to push the message to.
     * @param message The message to be pushed.
     * @throws QueueException Any issues sending this message.
     */
    void publish(String queueName, byte[] message) throws QueueException;

    /** Starts listening to the given queue.
     *
     * @param queue     The queue to listen to.
     * @param action    The event handler.
     * @throws QueueException Any issues registering this event handler.
     */
    void listen(String queue, Function<byte[], Boolean> action) throws QueueException;

    /** Closes the connection to the server.
     * This MUST NOT throw an exception. Implementations may log exceptions via the appropriate logging framework for the MQ. If the MQ does not specify a framework JUL should be used.
     */
    void close();
}
