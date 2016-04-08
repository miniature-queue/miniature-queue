package com.github.mlk.queue.codex;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StringDecoderTest {

    @Test
    public void decodesUtfStrings() {
        String expected = "abc123\u2345";
        StringDecoder subject = new StringDecoder();
        String actual = (String) subject.decode(expected.getBytes(Charset.forName("UTF-8")), String.class);

        assertThat(actual, is(expected));
    }

    @Test
    public void canDecodeStrings() {
        StringDecoder subject = new StringDecoder();

        assertTrue(subject.canHandle(String.class));
    }

    @Test
    public void canNotDecodeNoneStrings() {
        StringDecoder subject = new StringDecoder();

        assertFalse(subject.canHandle(StringBuilder.class));
    }
}