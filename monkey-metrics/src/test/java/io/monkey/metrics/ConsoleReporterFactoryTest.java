package io.monkey.metrics;

import io.monkey.jackson.DiscoverableSubtypeResolver;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsoleReporterFactoryTest {
    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
                .contains(ConsoleReporterFactory.class);
    }
}
