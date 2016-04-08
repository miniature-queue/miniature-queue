package com.github.mlk.queue.codex;

import com.github.mlk.queue.CodexException;
import org.junit.Test;

import java.io.Serializable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SerializationTest {

    @Test
    public void encodeDecodeSimpleTest() {
        SerializationEncoder encoder = new SerializationEncoder();
        SerializationDecoder decoder = new SerializationDecoder();

        String expected = "actual";
        String actual = (String) decoder.decode(encoder.encode(expected), String.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void encodingSerializationExceptionWrapped() {
        SerializationEncoder encoder = new SerializationEncoder();
        NotSerializable expected = new NotSerializable();
        CodexException exception = null;
        try {
            encoder.encode(expected);
        } catch (CodexException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertThat(exception.getActual(), is(expected));
    }

    @Test
    public void decodingSerializationExceptionWrapped() {
        SerializationDecoder decoder = new SerializationDecoder();
        byte[] expected = new byte[0];
        CodexException exception = null;
        try {
            decoder.decode(expected, String.class);
        } catch (CodexException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertThat(exception.getActual(), is(expected));
    }

    @Test
    public void encodingSupportsSerializable() {
        SerializationEncoder encoder = new SerializationEncoder();
        assertTrue(encoder.canHandle(IsSerializable.class));
        assertTrue(encoder.canHandle(IsSerializableExtends.class));
        assertTrue(encoder.canHandle(IsSerializableImplements.class));
    }

    @Test
    public void decoderSupportsSerializable() {
        SerializationDecoder decoder = new SerializationDecoder();
        assertTrue(decoder.canHandle(IsSerializable.class));
        assertTrue(decoder.canHandle(IsSerializableExtends.class));
        assertTrue(decoder.canHandle(IsSerializableImplements.class));
    }

    @Test
    public void encodingDoesNotSupportUnSerializable() {
        SerializationEncoder encoder = new SerializationEncoder();
        assertFalse(encoder.canHandle(NotSerializable.class));
    }

    @Test
    public void decoderDoesNotSupportUnSerializable() {
        SerializationDecoder decoder = new SerializationDecoder();
        assertFalse(decoder.canHandle(NotSerializable.class));
    }

    private static class NotSerializable {}
    private static class IsSerializable implements Serializable {}
    private static class IsSerializableExtends extends IsSerializable {}
    private interface ImSerializable extends Serializable {}
    private static class IsSerializableImplements implements ImSerializable {}
}