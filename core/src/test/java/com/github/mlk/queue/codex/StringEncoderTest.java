package com.github.mlk.queue.codex;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class StringEncoderTest {
    @Test
    public void decodesUtfStrings() {
        String expected = "abc123\u2345";
        StringEncoder subject = new StringEncoder();
        byte[] actual = subject.encode(expected);

        assertThat(actual, is(expected.getBytes(Charset.forName("UTF-8"))));
    }

    @Test
    public void canDecodeStrings() {
        StringEncoder subject = new StringEncoder();

        assertTrue(subject.canHandle(String.class));
    }

    @Test
    public void canNotDecodeNoneStrings() {
        StringEncoder subject = new StringEncoder();

        assertFalse(subject.canHandle(StringBuilder.class));
    }
}