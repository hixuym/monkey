package io.sunflower.guicey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class LoggerProvider implements Provider<Logger> {
    private final Logger applicationWideLogger;

    @Inject
    public LoggerProvider() {
        this.applicationWideLogger = LoggerFactory.getLogger("sunflower");
    }

    public Logger get() {
        return applicationWideLogger;
    }
}
