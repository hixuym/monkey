package io.sunflower.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class UrlConfigurationSourceProviderTest {

  private final ConfigurationSourceProvider provider = new UrlConfigurationSourceProvider();

  @Test
  public void readsFileContents() throws Exception {
    try (InputStream input = provider.open(Resources.getResource("example.txt").toString())) {
      assertThat(new String(ByteStreams.toByteArray(input), StandardCharsets.UTF_8).trim())
          .isEqualTo("whee");
    }
  }
}
