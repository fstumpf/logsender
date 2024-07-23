package org.example.logsender.reader;

import java.util.stream.Stream;

public interface MessageReader {
    Stream<String> streamContent();
}
