package io.monkey.configuration;

import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceConfigurationSourceProviderTest {
    private final ConfigurationSourceProvider provider = new ResourceConfigurationSourceProvider();

    @Test
    public void readsFileContents() throws Exception {
        assertForWheeContent("example.txt");
        assertForWheeContent("io/monkey/configuration/not-root-example.txt");
        assertForWheeContent("/io/monkey/configuration/not-root-example.txt");
    }

    private void assertForWheeContent(String path) throws Exception {
        assertThat(loadResourceAsString(path)).isEqualToIgnoringWhitespace("whee");
    }

    private String loadResourceAsString(String path) throws Exception {
        try (InputStream inputStream = provider.open(path)) {
            return CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
    }
}
