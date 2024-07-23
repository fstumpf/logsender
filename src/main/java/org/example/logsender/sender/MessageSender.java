package org.example.logsender.sender;

import com.fasterxml.jackson.databind.JsonNode;

public interface MessageSender {
    void sendMessage(JsonNode msg);

    void flush();
}
