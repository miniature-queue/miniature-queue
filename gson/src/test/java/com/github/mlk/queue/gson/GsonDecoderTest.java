package com.github.mlk.queue.gson;

import com.github.mlk.queue.CodexException;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GsonDecoderTest {
    @Test
    public void happyPath() {
        GsonDecoder decoder = new GsonDecoder();
        GsonEncoder encoder = new GsonEncoder();

        assertThat(decoder.decode(encoder.encode("Fred"), String.class), is("Fred"));
    }

    @Test(expected = CodexException.class)
    public void readExceptionsAreWrapped() {
        new GsonDecoder().decode("not json".getBytes(), String.class);
    }

    @Test(expected = CodexException.class)
    public void writeExceptionsAreWrapped() {
        new GsonEncoder().encode(Class.class);
    }

    @Test
    public void canHandleAnything() {
        assertTrue(new GsonDecoder().canHandle(this.getClass()));
        assertTrue(new GsonEncoder().canHandle(this.getClass()));
    }
}
