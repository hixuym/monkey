package io.sunflower.configuration;

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.text.StrSubstitutor;

/**
 * A delegating {@link ConfigurationSourceProvider} which replaces variables in the underlying
 * configuration source according to the rules of a custom {@link org.apache.commons.lang3.text.StrSubstitutor}.
 */
public class SubstitutingSourceProvider implements ConfigurationSourceProvider {

  private final ConfigurationSourceProvider delegate;
  private final StrSubstitutor substitutor;

  /**
   * Create a new instance.
   *
   * @param delegate The underlying {@link ConfigurationSourceProvider}.
   * @param substitutor The custom {@link StrSubstitutor} implementation.
   */
  public SubstitutingSourceProvider(ConfigurationSourceProvider delegate,
      StrSubstitutor substitutor) {
    this.delegate = requireNonNull(delegate);
    this.substitutor = requireNonNull(substitutor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream open(String path) throws IOException {
    try (final InputStream in = delegate.open(path)) {
      final String config = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
      final String substituted = substitutor.replace(config);

      return new ByteArrayInputStream(substituted.getBytes(StandardCharsets.UTF_8));
    }
  }
}
