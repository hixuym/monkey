package io.sunflower.logging;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ThrowableProxy;
import java.io.IOException;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

public class PrefixedExtendedThrowableProxyConverterTest {

  private final PrefixedExtendedThrowableProxyConverter converter = new PrefixedExtendedThrowableProxyConverter();
  private final ThrowableProxy proxy = new ThrowableProxy(new IOException("noo"));

  @Before
  public void setup() {
    converter.setOptionList(Collections.singletonList("full"));
    converter.start();
  }

  @Test
  public void prefixesExceptionsWithExclamationMarks() throws Exception {
    assertThat(converter.throwableProxyToString(proxy))
        .startsWith(String.format("! java.io.IOException: noo%n" +
            "! at io.sunflower.logging.PrefixedExtendedThrowableProxyConverterTest.<init>(PrefixedExtendedThrowableProxyConverterTest.java:14)%n"));
  }
}
