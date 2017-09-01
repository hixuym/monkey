package io.sunflower.logging;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Individual {@link Logger} configuration
 */
public class LoggerConfiguration {

    @NotNull
    private String level = "INFO";

    @Valid
    @NotNull
    private ImmutableList<AppenderFactory<ILoggingEvent>> appenders = ImmutableList.of();

    private boolean additive = true;

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ImmutableList<AppenderFactory<ILoggingEvent>> getAppenders() {
        return appenders;
    }

    public void setAppenders(List<AppenderFactory<ILoggingEvent>> appenders) {
        this.appenders = ImmutableList.copyOf(appenders);
    }
}
