package io.sunflower.setup;

import static java.util.Objects.requireNonNull;

import java.util.SortedMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.validation.Validator;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.SharedHealthCheckRegistries;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.json.HealthCheckModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;
import io.sunflower.guicey.setup.GuiceyEnvironment;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.lifecycle.setup.LifecycleEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sunflower application's environment.
 */
public class Environment {

  private final String name;
  private final MetricRegistry metricRegistry;
  private final HealthCheckRegistry healthCheckRegistry;

  private final ObjectMapper objectMapper;
  private final XmlMapper xmlMapper;

  private Validator validator;

  private final LifecycleEnvironment lifecycleEnvironment;
  private final GuiceyEnvironment guiceyEnvironment;

  private final ExecutorService healthCheckExecutorService;
  private final ClassLoader classLoader;

  /**
   * Creates a new environment.
   *
   * @param name the name of the application
   * @param objectMapper the {@link ObjectMapper} for the application
   */
  public Environment(String name,
      ObjectMapper objectMapper,
      XmlMapper xmlMapper,
      Validator validator,
      MetricRegistry metricRegistry,
      ClassLoader classLoader,
      HealthCheckRegistry healthCheckRegistry) {
    this.name = name;
    this.objectMapper = objectMapper;
    this.xmlMapper = xmlMapper;

    this.metricRegistry = metricRegistry;
    this.healthCheckRegistry = healthCheckRegistry;

    this.healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());

    this.validator = validator;
    this.classLoader = classLoader;

    this.guiceyEnvironment = new GuiceyEnvironment();

    this.guiceyEnvironment.install(new BootModule(this));

    this.lifecycleEnvironment = new LifecycleEnvironment();

    lifecycleEnvironment.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
      @Override
      public void lifeCycleStarting(LifeCycle event) {
        logHealthChecks();
      }
    });

    this.healthCheckExecutorService = this.lifecycle()
        .executorService("TimeBoundHealthCheck-pool-%d")
        .workQueue(new ArrayBlockingQueue<>(1))
        .minThreads(1)
        .maxThreads(4)
        .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
        .rejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy())
        .build();

    try {
      SharedMetricRegistries.getDefault();
    } catch (IllegalStateException e) {
      SharedMetricRegistries.setDefault("default", metricRegistry);
    }
    try {
      SharedHealthCheckRegistries.getDefault();
    } catch (IllegalStateException e) {
      SharedHealthCheckRegistries.setDefault("default", healthCheckRegistry);
    }
  }

  /**
   * Creates an environment with default health check registry
   */
  public Environment(String name,
      ObjectMapper objectMapper,
      XmlMapper xmlMapper,
      Validator validator,
      MetricRegistry metricRegistry,
      ClassLoader classLoader) {
    this(name, objectMapper, xmlMapper, validator, metricRegistry, classLoader,
        new HealthCheckRegistry());
  }

  /**
   * Returns an {@link ExecutorService} to run time bound health checks
   */
  public ExecutorService getHealthCheckExecutorService() {
    return healthCheckExecutorService;
  }

  /**
   * Returns the application's {@link LifecycleEnvironment}.
   */
  public LifecycleEnvironment lifecycle() {
    return lifecycleEnvironment;
  }

  /**
   * Returns the application's {@link ObjectMapper}.
   */
  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public XmlMapper getXmlMapper() {
    return xmlMapper;
  }

  /**
   * Returns the application's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the application's {@link Validator}.
   */
  public Validator getValidator() {
    return validator;
  }

  /**
   * Sets the application's {@link Validator}.
   */
  public void setValidator(Validator validator) {
    this.validator = requireNonNull(validator);
  }

  /**
   * Returns the application's {@link MetricRegistry}.
   */
  public MetricRegistry metrics() {
    return metricRegistry;
  }

  public GuiceyEnvironment guicey() {
    return guiceyEnvironment;
  }

  public Injector injector() {
    return guiceyEnvironment.getInjector();
  }

  /**
   * @return application's {@link ClassLoader}.
   */
  public ClassLoader classLoader() {
    return this.classLoader;
  }

  /**
   * Returns the application's {@link HealthCheckRegistry}.
   */
  public HealthCheckRegistry healthChecks() {
    return healthCheckRegistry;
  }

  private static Logger LOGGER = LoggerFactory.getLogger(Environment.class);

  private void logHealthChecks() {
    if (healthChecks().getNames().size() <= 1) {
      LOGGER.warn(String.format(
          "%n" +
              "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
              "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
              "!    THIS APPLICATION HAS NO HEALTHCHECKS. THIS MEANS YOU WILL NEVER KNOW      !%n" +
              "!     IF IT DIES IN PRODUCTION, WHICH MEANS YOU WILL NEVER KNOW IF YOU'RE      !%n" +
              "!    LETTING YOUR USERS DOWN. YOU SHOULD ADD A HEALTHCHECK FOR EACH OF YOUR    !%n" +
              "!         APPLICATION'S DEPENDENCIES WHICH FULLY (BUT LIGHTLY) TESTS IT.       !%n" +
              "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
              "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
      ));
    }
    LOGGER.info("health checks = {}", healthChecks().getNames());
  }
}
