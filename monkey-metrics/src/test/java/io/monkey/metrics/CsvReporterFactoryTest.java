package io.monkey.metrics;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.monkey.configuration.YamlConfigurationFactory;
import io.monkey.jackson.DiscoverableSubtypeResolver;
import io.monkey.jackson.Jackson;
import io.monkey.lifecycle.setup.LifecycleEnvironment;
import io.monkey.validation.BaseValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvReporterFactoryTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final YamlConfigurationFactory<MetricsFactory> factory =
            new YamlConfigurationFactory<>(MetricsFactory.class,
                                           BaseValidator.newValidator(),
                                           objectMapper, "mk");

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
