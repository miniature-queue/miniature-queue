package com.github.mlk.queue.gson;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.implementation.Module;

public class GsonModule implements Module {
    public static GsonModule gson() {
        return new GsonModule();
    }

    @Override
    public void bind(Queuify.Builder builder) {
        builder.encoder(new GsonEncoder())
                .decoder(new GsonDecoder());
    }
}
