package io.sunflower.metrics;

import org.junit.Test;

import io.sunflower.jackson.DiscoverableSubtypeResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class Slf4jReporterFactoryTest {
    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
            .contains(Slf4jReporterFactory.class);
    }
}
