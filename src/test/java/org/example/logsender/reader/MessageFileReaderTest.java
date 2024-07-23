package org.example.logsender.reader;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageFileReaderTest {

    @Test
    void readSimpleFile() {
        final MessageFileReader reader = new MessageFileReader("src/test/resources/one_to_five.txt");
        final List<String> content = reader.streamContent().toList();
        assertEquals(List.of("1", "2", "3", "4", "5"), content);
    }

    @Test
    void emptyStreamForNonExistingFile() {
        final MessageFileReader reader = new MessageFileReader("this/file/doesnt/exist.txt");
        final List<String> content = reader.streamContent().toList();
        assertTrue(content.isEmpty());
    }
}