package org.example.logsender.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MessageFileReader implements MessageReader {
    public static final Logger log = LoggerFactory.getLogger(MessageFileReader.class);
    private final String filename;

    public MessageFileReader(final String filename) {
        this.filename = filename;
    }

    public Stream<String> streamContent() {
        try {
            return Files.lines(Path.of(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("File not found: {}", new File(filename).getAbsolutePath());
            return Stream.empty();
        }
    }
}
