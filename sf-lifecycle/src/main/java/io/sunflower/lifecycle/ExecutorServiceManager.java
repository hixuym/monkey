package io.sunflower.lifecycle;

import java.util.concurrent.ExecutorService;

import io.sunflower.util.Duration;

public class ExecutorServiceManager extends AbstractLifeCycle {

  private final ExecutorService executor;
  private final Duration shutdownPeriod;
  private final String poolName;

  public ExecutorServiceManager(ExecutorService executor, Duration shutdownPeriod,
      String poolName) {
    this.executor = executor;
    this.shutdownPeriod = shutdownPeriod;
    this.poolName = poolName;
  }

  @Override
  public void doStop() throws Exception {
    executor.shutdown();
    executor.awaitTermination(shutdownPeriod.getQuantity(), shutdownPeriod.getUnit());
  }

  @Override
  public String toString() {
    return super.toString() + '(' + poolName + ')';
  }

}
