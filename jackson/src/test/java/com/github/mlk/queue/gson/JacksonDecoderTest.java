package com.github.mlk.queue.gson;

import com.github.mlk.queue.CodexException;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JacksonDecoderTest {
    @Test
    public void happyPath() {
        JacksonDecoder decoder = new JacksonDecoder();
        JacksonEncoder encoder = new JacksonEncoder();

        assertThat(decoder.decode(encoder.encode("Fred"), String.class), is("Fred"));
    }

    @Test(expected = CodexException.class)
    public void readExceptionsAreWrapped() {
        new JacksonDecoder().decode("not json".getBytes(), String.class);
    }

    @Test(expected = CodexException.class)
    public void writeExceptionsAreWrapped() {
        new JacksonEncoder().encode(this);
    }

    @Test
    public void canHandleAnything() {
        assertTrue(new JacksonDecoder().canHandle(this.getClass()));
        assertTrue(new JacksonEncoder().canHandle(this.getClass()));
    }
}
