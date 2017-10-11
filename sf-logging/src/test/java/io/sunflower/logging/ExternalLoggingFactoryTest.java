package io.sunflower.logging;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.Resources;
import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.DiscoverableSubtypeResolver;
import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;
import java.io.File;
import org.junit.Test;

public class ExternalLoggingFactoryTest {

  @Test
  public void canBeDeserialized() throws Exception {
    LoggingFactory externalRequestLogFactory = new YamlConfigurationFactory<>(LoggingFactory.class,
        BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw")
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
