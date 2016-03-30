package com.github.mlk.queue.gson;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GsonDecoderTest {
    @Test
    public void happyPath() {
        GsonDecoder decoder = new GsonDecoder();
        GsonEncoder encoder = new GsonEncoder();

        assertThat(decoder.decode(encoder.encode("Fred"), String.class), is((Object)"Fred"));
    }

}
