package com.github.mlk.queue.codex;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.implementation.Module;

public class Utf8StringModule implements Module {
    public static Utf8StringModule utfStrings() {
        return new Utf8StringModule();
    }

    @Override
    public void bind(Queuify.Builder builder) {
        builder.encoder(new StringEncoder())
                .decoder(new StringDecoder());
    }
}
