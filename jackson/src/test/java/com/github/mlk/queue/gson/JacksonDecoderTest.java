package com.github.mlk.queue.gson;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JacksonDecoderTest {
    @Test
    public void happyPath() {
        JacksonDecoder decoder = new JacksonDecoder();
        JacksonEncoder encoder = new JacksonEncoder();

        assertThat(decoder.decode(encoder.encode("Fred"), String.class), is("Fred"));
    }

}
