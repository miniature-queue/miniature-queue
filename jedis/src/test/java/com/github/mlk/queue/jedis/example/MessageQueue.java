package com.github.mlk.queue.jedis.example;

import com.github.mlk.queue.Handle;
import com.github.mlk.queue.Publish;
import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueType;

import java.util.function.Function;

@Queue(value = "messages-fanout", queueTypeHint = QueueType.FANOUT_QUEUE)
public interface MessageQueue {
    @Publish
    void publishMessage(String message);

    @Handle
    void receiveMessage(Function<String, Boolean> function);
}
