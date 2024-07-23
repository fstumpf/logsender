package org.example.logsender.sender;

import com.fasterxml.jackson.databind.JsonNode;
import org.graylog2.gelfclient.GelfConfiguration;
import org.graylog2.gelfclient.GelfMessageBuilder;
import org.graylog2.gelfclient.GelfTransports;
import org.graylog2.gelfclient.transport.GelfTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class GelfSender implements MessageSender {
    public static final Logger logger = LoggerFactory.getLogger(GelfSender.class);
    final GelfConfiguration config;

    final GelfTransport transport;
    final String host;

    public GelfSender(final GelfConfiguration config) {
        this.config = config;

        transport = GelfTransports.create(config);
        String tempHost;
        try {
            tempHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Cannot find hostname! Continuing with \"localhost\".");
            tempHost = "localhost";
        }

        host = tempHost;
    }

    public void sendMessage(final JsonNode msg) {
        final GelfMessageBuilder builder = new GelfMessageBuilder(msg.toString(), host);
        msg.fields().forEachRemaining(e -> builder.additionalField(e.getKey(), e.getValue().asText()));

        // Blocks until there is capacity in the queue
        try {
            transport.send(builder.build());
        } catch (InterruptedException e) {
            logger.info("Sending message was interrupted! Message not sent.");
        }
    }

    public void flush() {
        transport.flushAndStopSynchronously(1, TimeUnit.SECONDS, 5);
    }

}
