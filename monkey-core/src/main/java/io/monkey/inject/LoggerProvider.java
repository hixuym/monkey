package io.monkey.inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author michael
 */
public class LoggerProvider implements Provider<Logger> {

    private final Logger applicationWideLogger;

    @Inject
    public LoggerProvider() {
        this.applicationWideLogger = LoggerFactory.getLogger("monkey");
    }

    @Override
    public Logger get() {
        return applicationWideLogger;
    }
}
