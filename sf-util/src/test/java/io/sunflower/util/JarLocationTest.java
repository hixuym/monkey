package io.sunflower.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

public class JarLocationTest {

  @Test
  public void isHumanReadable() throws Exception {
    assertThat(new JarLocation(JarLocationTest.class).toString())
        .isEqualTo("project.jar");
  }

  @Test
  public void hasAVersion() throws Exception {
    assertThat(new JarLocation(JarLocationTest.class).getVersion())
        .isEqualTo(Optional.empty());
  }
}
