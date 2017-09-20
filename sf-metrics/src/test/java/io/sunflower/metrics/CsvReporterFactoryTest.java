package io.sunflower.metrics;

import com.google.common.io.Resources;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.DiscoverableSubtypeResolver;
import io.sunflower.jackson.Jackson;
import io.sunflower.lifecycle.setup.LifecycleEnvironment;
import io.sunflower.validation.BaseValidator;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvReporterFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final YamlConfigurationFactory<MetricsFactory> factory =
        new YamlConfigurationFactory<>(MetricsFactory.class,
            BaseValidator.newValidator(),
            objectMapper, "sf");

    @Before
    public void setUp() throws Exception {
        objectMapper.getSubtypeResolver().registerSubtypes(ConsoleReporterFactory.class,
            CsvReporterFactory.class,
            Slf4jReporterFactory.class);
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
            .contains(CsvReporterFactory.class);
    }

    @Test
    public void directoryCreatedOnStartup() throws Exception {
        File dir = new File("metrics");
        dir.delete();

        MetricsFactory config = factory.build(new File(Resources.getResource("yaml/metrics.yml").toURI()));
        config.configure(new LifecycleEnvironment(), new MetricRegistry());
        assertThat(dir.exists()).isEqualTo(true);
    }
}
