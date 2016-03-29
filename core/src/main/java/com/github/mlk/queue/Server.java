package com.github.mlk.queue;

import com.github.mlk.queue.implementation.ServerImplementation;

public abstract class Server {
    abstract ServerImplementation getImplementation();
}
