package com.github.mrchris2000.queue.mqlight;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.mrchris2000.queue.mqlight.implementation.MqLightServerImplementation;
import com.ibm.mqlight.api.CompletionListener;
import com.ibm.mqlight.api.NonBlockingClient;
import com.ibm.mqlight.api.NonBlockingClientAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;


public class MqLightServer extends Server {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final MqLightServerImplementation implementation;

    /** @param host The host (service in MQ Light parlance) to connect to. This will result in a default connection with this host.
     */
    public MqLightServer(String host) {
        NonBlockingClient client = NonBlockingClient.create(host, new NonBlockingClientAdapter() {
        }, null);
        client.start(new CompletionListener() {
            public void onSuccess(NonBlockingClient client,  // c == client
                                  Object ctx) {
                // ... code for handling success of send operation
                logger.log(Level.INFO, "MQ Light client completion");
            }

            public void onError(NonBlockingClient client, Object o, Exception e) {
                logger.log(Level.WARNING, "MQ Light client failed: " +e.getMessage() );
            }
        }, null);
        implementation = new MqLightServerImplementation(client);

    }

    /** @param client A pre-constructed connection for MQ Light.
     */
    public MqLightServer(NonBlockingClient client) {
        implementation = new MqLightServerImplementation(client);
    }

    @Override
    protected ServerImplementation getImplementation() {
        return implementation;
    }
}
