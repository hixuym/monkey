package io.sunflower.logging.layout;

import java.util.TimeZone;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import io.sunflower.logging.SunflowerLayout;

/**
 * Factory that creates a {@link SunflowerLayout}
 */
public class SunflowerLayoutFactory implements LayoutFactory<ILoggingEvent> {
    @Override
    public PatternLayoutBase<ILoggingEvent> build(LoggerContext context, TimeZone timeZone) {
        return new SunflowerLayout(context, timeZone);
    }
}
