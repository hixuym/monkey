package io.sunflower.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.sunflower.jackson.DiscoverableSubtypeResolver;
import org.junit.Test;

public class Slf4jReporterFactoryTest {

  @Test
  public void isDiscoverable() throws Exception {
    assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
        .contains(Slf4jReporterFactory.class);
  }
}
