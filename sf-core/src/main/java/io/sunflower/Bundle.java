package io.sunflower;

import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * A reusable bundle of functionality, used to define blocks of application behavior.
 * @author michael
 */
public interface Bundle {

  /**
   * Initializes the application bootstrap.
   *
   * @param bootstrap the application bootstrap
   */
  void initialize(Bootstrap<?> bootstrap);

  /**
   * Initializes the application environment.
   *
   * @param environment the application environment
   */
  void run(Environment environment);
}
