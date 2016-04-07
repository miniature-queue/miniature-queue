REDIS using jedis
=================

This implements FANOUT only (Pub/Sub) for REDIS based queues. When attempting to use REDIS on worker queues then a 
FANOUT is created and a WARNING is raised.

```
MessageQueue mq = Queuify.builder().server(new JedisServer("localhost")).target(MessageQueue.class);
// Set up a listener for new messages.
mq.receiveMessage((x) -> { System.out.println(x); return true; } );
// Sends hello down the line
mq.publishMessage("Hello");
```

String Encoding
---------------
REDIS sends messages as Strings rather than byte arrays. As such objects are sent Base64-encoded. This can be changed by
setting the codex to `UtfStringToString`. This will only work if the Queue was created with a String based 
encoder/decoder, for example a JSON. It will not work with binary based formats (Base64 will).

Acknowledgements
----------------
REDIS does not support an acknowledgement process, as such the return value is ignored. 
