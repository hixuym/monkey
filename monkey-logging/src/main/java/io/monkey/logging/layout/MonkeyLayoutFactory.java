package io.monkey.logging.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import io.monkey.logging.MonkeyLayout;

import java.util.TimeZone;

/**
 * Factory that creates a {@link MonkeyLayout}
 * @author michael
 */
public class MonkeyLayoutFactory implements LayoutFactory<ILoggingEvent> {

    @Override
    public PatternLayoutBase<ILoggingEvent> build(LoggerContext context, TimeZone timeZone) {
        return new MonkeyLayout(context, timeZone);
    }
}
