package io.sunflower.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.sunflower.jackson.DiscoverableSubtypeResolver;
import org.junit.Test;

public class ConsoleReporterFactoryTest {

  @Test
  public void isDiscoverable() throws Exception {
    assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
        .contains(ConsoleReporterFactory.class);
  }
}
