package io.monkey.logging;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.monkey.jackson.DiscoverableSubtypeResolver;
import io.monkey.logging.async.AsyncLoggingEventAppenderFactory;
import io.monkey.logging.filter.NullLevelFilterFactory;
import io.monkey.logging.layout.MonkeyLayoutFactory;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsoleAppenderFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(ConsoleAppenderFactory.class);
    }

    @Test
    public void includesCallerData() {
        ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory = new ConsoleAppenderFactory<>();
        AsyncAppender asyncAppender = (AsyncAppender) consoleAppenderFactory.build(new LoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());
        assertThat(asyncAppender.isIncludeCallerData()).isFalse();

        consoleAppenderFactory.setIncludeCallerData(true);
        asyncAppender = (AsyncAppender) consoleAppenderFactory.build(new LoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());
        assertThat(asyncAppender.isIncludeCallerData()).isTrue();
    }

    @Test
    public void appenderContextIsSet() throws Exception {
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        final ConsoleAppenderFactory<ILoggingEvent> appenderFactory = new ConsoleAppenderFactory<>();
        final Appender<ILoggingEvent> appender = appenderFactory.build(root.getLoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());

        assertThat(appender.getContext()).isEqualTo(root.getLoggerContext());
    }

    @Test
    public void appenderNameIsSet() throws Exception {
        final Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        final ConsoleAppenderFactory<ILoggingEvent> appenderFactory = new ConsoleAppenderFactory<>();
        final Appender<ILoggingEvent> appender = appenderFactory.build(root.getLoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());

        assertThat(appender.getName()).isEqualTo("async-console-appender");
    }
    
    @Test
    public void isNeverBlock() throws Exception {
        ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory = new ConsoleAppenderFactory<>();
        consoleAppenderFactory.setNeverBlock(true);
        AsyncAppender asyncAppender = (AsyncAppender) consoleAppenderFactory.build(new LoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());

        assertThat(asyncAppender.isNeverBlock()).isTrue();
    }
    
    @Test
    public void isNotNeverBlock() throws Exception {
        ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory = new ConsoleAppenderFactory<>();
        consoleAppenderFactory.setNeverBlock(false);
        AsyncAppender asyncAppender = (AsyncAppender) consoleAppenderFactory.build(new LoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());

        assertThat(asyncAppender.isNeverBlock()).isFalse();
    }
    
    @Test
    public void defaultIsNotNeverBlock() throws Exception {
        ConsoleAppenderFactory<ILoggingEvent> consoleAppenderFactory = new ConsoleAppenderFactory<>();
        // default neverBlock
        AsyncAppender asyncAppender = (AsyncAppender) consoleAppenderFactory.build(new LoggerContext(), "test", new MonkeyLayoutFactory(), new NullLevelFilterFactory<>(), new AsyncLoggingEventAppenderFactory());

        assertThat(asyncAppender.isNeverBlock()).isFalse();
    }
    
}
