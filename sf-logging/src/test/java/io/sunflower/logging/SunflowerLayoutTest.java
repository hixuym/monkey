package io.sunflower.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.TimeZone;

import ch.qos.logback.classic.LoggerContext;
import org.junit.Test;

public class SunflowerLayoutTest {

  private final LoggerContext context = mock(LoggerContext.class);
  private final TimeZone timeZone = TimeZone.getTimeZone("UTC");
  private final SunflowerLayout layout = new SunflowerLayout(context, timeZone);

  @Test
  public void prefixesThrowables() throws Exception {
    assertThat(layout.getDefaultConverterMap().get("ex"))
        .isEqualTo(PrefixedThrowableProxyConverter.class.getName());
  }

  @Test
  public void prefixesExtendedThrowables() throws Exception {
    assertThat(layout.getDefaultConverterMap().get("xEx"))
        .isEqualTo(PrefixedExtendedThrowableProxyConverter.class.getName());
  }

  @Test
  public void hasAContext() throws Exception {
    assertThat(layout.getContext())
        .isEqualTo(context);
  }

  @Test
  public void hasAPatternWithATimeZoneAndExtendedThrowables() throws Exception {
    assertThat(layout.getPattern())
        .isEqualTo("%-5p [%d{ISO8601,UTC}] %c: %m%n%rEx");
  }
}
