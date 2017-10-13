package io.sunflower.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;
import org.junit.Test;

public class ResourceConfigurationSourceProviderTest {

  private final ConfigurationSourceProvider provider = new ResourceConfigurationSourceProvider();

  @Test
  public void readsFileContents() throws Exception {
    assertForWheeContent("example.txt");
    assertForWheeContent("io/sunflower/configuration/not-root-example.txt");
    assertForWheeContent("/io/sunflower/configuration/not-root-example.txt");
  }

  private void assertForWheeContent(String path) throws Exception {
    assertThat(loadResourceAsString(path)).isEqualTo("whee");
  }

  private String loadResourceAsString(String path) throws Exception {
    try (InputStream input = provider.open(path)) {
      return new String(ByteStreams.toByteArray(input), StandardCharsets.UTF_8).trim();
    }
  }
}
