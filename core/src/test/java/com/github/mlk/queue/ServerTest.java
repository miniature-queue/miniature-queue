package com.github.mlk.queue;

import com.github.mlk.queue.implementation.ServerImplementation;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerTest {
    static class StubServer extends Server {
        private final ServerImplementation implementation;

        StubServer(ServerImplementation implementation) {
            this.implementation = implementation;
        }

        @Override
        protected ServerImplementation getImplementation() {
            return implementation;
        }
    }

    @Test
    public void closePassesCloseToImplementation() {
        StubServer server = new StubServer(mock(ServerImplementation.class));

        server.close();;

        verify(server.getImplementation()).close();
    }
}