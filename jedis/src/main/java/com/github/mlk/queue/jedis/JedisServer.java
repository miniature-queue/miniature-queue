package com.github.mlk.queue.jedis;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mlk.queue.jedis.codex.Base64ToString;
import com.github.mlk.queue.jedis.codex.ToString;
import com.github.mlk.queue.jedis.implementation.JedisServerImplementation;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ThreadFactory;

public class JedisServer extends Server {

    private final JedisServerImplementation implementation;

    /** @param host The host to connect to. This will result in a default connection factory with this host.
     */
    public JedisServer(String host) {
        JedisPool jedis = new JedisPool(new JedisPoolConfig(), host);

        implementation = new JedisServerImplementation(jedis, Thread::new, new Base64ToString());
    }

    /** @param jedis The connection factory for JEDIS.
     *  @param threadFactory How to create the (single) thread for listening for messages.
     *  @param codex How to convert a byte array to a string.
     */
    public JedisServer(JedisPool jedis, ThreadFactory threadFactory, ToString codex) {
        implementation = new JedisServerImplementation(jedis, threadFactory, codex);
    }

    /** @param jedis The connection factory for JEDIS.
     */
    public JedisServer(JedisPool jedis) {
        implementation = new JedisServerImplementation(jedis, Thread::new, new Base64ToString());
    }

    @Override
    protected ServerImplementation getImplementation() {
        return implementation;
    }
}
