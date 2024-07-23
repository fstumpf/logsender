package org.example.logsender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logsender.reader.MessageReader;
import org.example.logsender.sender.MessageSender;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class LogSenderTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readAndSend() throws IOException {
        final MessageReader reader = mock(MessageReader.class);
        final MessageSender sender = mock(MessageSender.class);
        final String one = "{\"one\": 1}";
        final String two = "{\"two\": 2}";

        when(reader.streamContent()).thenReturn(Stream.of(one, two));

        final LogSender testee = new LogSender(reader, sender);

        testee.startWorking();

        verify(reader).streamContent();
        verify(sender).sendMessage(eq(mapper.readTree(one)));
        verify(sender).sendMessage(eq(mapper.readTree(two)));
    }

    @Test
    void filterInvalidMessages() throws IOException {
        final MessageReader reader = mock(MessageReader.class);
        final MessageSender sender = mock(MessageSender.class);
        final String one = "{\"one\": 1}";
        final String two = "{malFormedNode: \"some[Value}}";

        when(reader.streamContent()).thenReturn(Stream.of(one, two));

        final LogSender testee = new LogSender(reader, sender);

        testee.startWorking();

        verify(reader).streamContent();
        verify(sender).sendMessage(eq(mapper.readTree(one)));
    }
}
