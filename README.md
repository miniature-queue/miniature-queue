# miniature-queue

[![Join the chat at https://gitter.im/mlk/miniature-queue](https://badges.gitter.im/mlk/miniature-queue.svg)](https://gitter.im/mlk/miniature-queue?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Build Status](https://travis-ci.org/mlk/miniature-queue.svg?branch=master)](https://travis-ci.org/mlk/miniature-queue) [![codecov.io](https://codecov.io/github/mlk/miniature-queue/coverage.svg?branch=master)](https://codecov.io/github/mlk/miniature-queue?branch=master) [![API Docs](https://img.shields.io/badge/documentation-here-orange.svg)](http://pi.michael-lloyd-lee.me.uk/miniature-queue/apidocs/index.html) [![Dependency Status](https://www.versioneye.com/user/projects/5735b7dbebad9c000ef75e78/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5735b7dbebad9c000ef75e78)

`miniature-queue` is a Message Queue abstraction layer inspired by [feign](https://github.com/Netflix/feign) and [JDBI](http://jdbi.org/). It uses annotation based meta-programming to bind an interface to an Message Queue implementation.

## Example

```
@Queue("chat", queueTypeHint=QueueType.FANOUT_QUEUE)
interface Chat {
    @Publish
    void publishMessage(String message);
    @Handle
    void receiveMessage(Function<String, Boolean> action);
}

MessageQueue queue = Queuify.builder().server(createServerObject()).target(MessageQueue.class);
// Set up a listener for new messages.
queue.receiveMessage((x) -> { System.out.println(x); return true; } );
// Sends hello down the line
queue.publishMessage("Hello");
```

## Queue types

### Worker Queues
In a worker queue each message is sent to only one listener. The message publisher sends a fire-and-forget message out to the queue for the work to be done.

### Fanout (or Publish/Subscribe)
In a fanout queue each message is sent to every listener. The message publisher sends a fire-and-forget message out to the queue for the listners to be informed.


## Message Queues supported

 * [Rabbit MQ](https://www.rabbitmq.com/) via  [`minature-queue-rabbitmq`](https://github.com/mlk/miniature-queue/tree/master/rabbitmq) package.
 * [Redis](http://redis.io/) via the [`miniature-queue-jedis`](https://github.com/mlk/miniature-queue/tree/master/jedis) package. Note: This only supports FAN_OUT queues.
 * [MQLight](https://developer.ibm.com/messaging/mq-light/) via the [`miniature-queue-mqlight`](https://github.com/mlk/miniature-queue/tree/master/mqlight) package. Note: This is a work in progress.

## Encoding/Decoding objects

 * Java serialization (in core)
 * UTF-8 Strings (in core)
 * JSON via [GSON](https://github.com/google/gson) via [`miniature-queue-gson`](https://github.com/mlk/miniature-queue/tree/master/gson) package.
 * JSON via [Jackson](https://github.com/FasterXML/jackson) via the [`miniature-queue-jackson`](https://github.com/mlk/miniature-queue/tree/master/jackson) package.


[Maven Site](http://pi.michael-lloyd-lee.me.uk/miniature-queue/)
