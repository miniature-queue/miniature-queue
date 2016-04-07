package com.github.mlk.queue;

/** The type of queue to create.
 * Note: this is a hint, please see your queuing backend for how each is implemented,
 */
public enum QueueType {
    /** A worker queue will give only one handler instance a copy of the queue item. If the handler acknowledges the queue item
     * it is not passed passed to any other handler. If the queue item is not acknowledged in an acceptable time frame
     * then the queue item is passed to a different worker.
     */
    WORKER_QUEUE,
    /** A fanout (or Publish/Subscribe) queue will give every handler a copy of the queue item. If the queue item is not
     * acknowledged then it is up to the queue implementation to decide if and when to resend it.
     */
    FANOUT_QUEUE
}
