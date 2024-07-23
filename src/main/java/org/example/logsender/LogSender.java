package org.example.logsender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.inject.Inject;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.example.logsender.reader.MessageReader;
import org.example.logsender.sender.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LogSender {
    public static final Logger LOG = LoggerFactory.getLogger(LogSender.class);

    private final MessageReader reader;
    private final MessageSender sender;

    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public LogSender(final MessageReader reader, final MessageSender sender) {
        this.reader = reader;
        this.sender = sender;
    }

    public static void main(String[] args) throws IOException {
        final Config config = parseOptions(args);
        final Injector injector = Guice.createInjector(new GuiceBindings(config));

        final LogSender logSender = injector.getInstance(LogSender.class);

        LOG.debug("About to start processing log-file.");
        logSender.startWorking();
        LOG.info("Done processing log-file, waiting for the sender to finish flushing.");
        logSender.shutdown();
        LOG.info("Done waiting, shutting down everything.");
    }

    private static Config parseOptions(final String[] args) throws IOException {
        final OptionParser parser = new OptionParser();
        final OptionSpec<String> filename = parser
                .acceptsAll(List.of("f", "filename"))
                .withRequiredArg()
                .describedAs("The file to read messages from")
                .ofType(String.class);
        final OptionSpec<String> server = parser
                .acceptsAll(List.of("g", "graylogServer"))
                .withOptionalArg()
                .defaultsTo("localhost")
                .describedAs("The graylog server to send the messages to")
                .ofType(String.class);

        final OptionSet options = parser.parse(args);
        if (!options.has(filename)) {
            parser.printHelpOn(System.out);
            System.exit(1);
        }
        return new Config(filename.value(options), server.value(options));
    }

    public void startWorking() {
        final AtomicInteger count = new AtomicInteger(0);
        reader.streamContent()
                .peek(log -> count.incrementAndGet())
                .map(this::parse)
                .filter(node -> !NullNode.getInstance().equals(node))
                .forEach(sender::sendMessage);

        LOG.info("Read and sent {} lines. Waiting for the sender to finish.", count.get());
    }

    private JsonNode parse(final String content) {
        try {
            return mapper.readTree(content);
        } catch (final IOException e) {
            LOG.warn("Exception parsing lines to JSON: ", e);
            return NullNode.getInstance();
        }
    }


    private void shutdown() {
        sender.flush();
    }

}