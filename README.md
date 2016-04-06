# miniature-queue

[![Build Status](https://travis-ci.org/mlk/miniature-queue.svg?branch=master)](https://travis-ci.org/mlk/miniature-queue)

`miniature-queue` is a MQ abstraction layer inspired by [feign](https://github.com/Netflix/feign) and [JDBI](http://jdbi.org/).

## Example

```
@Queue("chat")
interface Chat {
    @Publish
    void publishMessage(String message);
    @Handle
    void receiveMessage(Function<String, Boolean> action);
}

MessageQueue mq = Queuify.builder().server(createServerObject()).target(MessageQueue.class);
// Set up a listener for new messages.
mq.receiveMessage((x) -> { System.out.println(x); return true; } );
// Sends hello down the line
mq.publishMessage("Hello");
```

## MQs support

Curently only RabbitMQ is supported in the `minature-queue-rabbitmq` package.

## Encoding/Decoding objects

Currently sending raw strings in UTF-8, Java Serialized objects and JSON (using GSON) is supported. Raw strings and Java Serialization are both supported natively, GSON is via the `minature-queue-gson` package.
