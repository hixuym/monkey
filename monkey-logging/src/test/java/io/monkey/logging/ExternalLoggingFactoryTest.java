package io.monkey.logging;

import com.google.common.io.Resources;
import io.monkey.configuration.YamlConfigurationFactory;
import io.monkey.json.DiscoverableSubtypeResolver;
import io.monkey.json.Jackson;
import io.monkey.validation.BaseValidator;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalLoggingFactoryTest {

    @Test
    public void canBeDeserialized() throws Exception {
        LoggingFactory externalRequestLogFactory = new YamlConfigurationFactory<>(LoggingFactory.class,
            BaseValidator.newValidator(), Jackson.newObjectMapper(), "mk")
            .build(new File(Resources.getResource("yaml/logging_external.yml").toURI()));
        assertThat(externalRequestLogFactory).isNotNull();
        assertThat(externalRequestLogFactory).isInstanceOf(ExternalLoggingFactory.class);
    }

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
            .contains(ExternalLoggingFactory.class);
    }
}
