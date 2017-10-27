package io.sunflower.guice;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
