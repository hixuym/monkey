package io.sunflower.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;

import java.util.TimeZone;

/**
 * A base layout for Dropwizard.
 * <ul>
 * <li>Disables pattern headers.</li>
 * <li>Prefixes logged exceptions with {@code !}.</li>
 * <li>Sets the pattern to the given timezone.</li>
 * </ul>
 * @author michael
 */
public class SunflowerLayout extends PatternLayout {

    public SunflowerLayout(LoggerContext context, TimeZone timeZone) {
        super();
        setOutputPatternAsHeader(false);
        getDefaultConverterMap().put("ex", PrefixedThrowableProxyConverter.class.getName());
        getDefaultConverterMap().put("xEx", PrefixedExtendedThrowableProxyConverter.class.getName());
        getDefaultConverterMap().put("rEx", PrefixedRootCauseFirstThrowableProxyConverter.class.getName());
        setPattern("%-5p [%d{ISO8601," + timeZone.getID() + "}] %c: %m%n%rEx");
        setContext(context);
    }
}
