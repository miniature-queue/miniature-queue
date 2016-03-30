package com.github.mlk.queue.gson;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.implementation.Module;

public class JacksonModule implements Module {
    public static JacksonModule jackson() {
        return new JacksonModule();
    }

    @Override
    public void bind(Queuify.Builder builder) {
        builder.encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
    }
}
