package org.example.logsender;

import com.google.inject.AbstractModule;
import org.example.logsender.reader.MessageFileReader;
import org.example.logsender.reader.MessageReader;
import org.example.logsender.sender.GelfSender;
import org.example.logsender.sender.MessageSender;
import org.graylog2.gelfclient.GelfConfiguration;
import org.graylog2.gelfclient.GelfTransports;

import java.net.InetSocketAddress;

public class GuiceBindings extends AbstractModule {
    private final Config config;

    public GuiceBindings(final Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(MessageReader.class).toInstance(new MessageFileReader(config.filename()));

        final GelfConfiguration gelfConfig = new GelfConfiguration(new InetSocketAddress(config.graylogServer(), 12201))
                .transport(GelfTransports.UDP)
                .queueSize(512)
                .connectTimeout(5000)
                .reconnectDelay(1000)
                .tcpNoDelay(true)
                .sendBufferSize(32768);
        bind(MessageSender.class).toInstance(new GelfSender(gelfConfig));
    }
}
