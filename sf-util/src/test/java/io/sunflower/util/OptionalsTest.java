package io.sunflower.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptionalsTest {

  @Test
  public void testFromGuavaOptional() throws Exception {
    assertFalse(Optionals.fromGuavaOptional(com.google.common.base.Optional.absent()).isPresent());
    assertTrue(Optionals.fromGuavaOptional(com.google.common.base.Optional.of("Foo")).isPresent());
    assertEquals(
        java.util.Optional.of("Foo"),
        Optionals.fromGuavaOptional(com.google.common.base.Optional.of("Foo"))
    );
  }

  @Test
  public void testToGuavaOptional() throws Exception {
    assertFalse(Optionals.toGuavaOptional(java.util.Optional.empty()).isPresent());
    assertTrue(Optionals.toGuavaOptional(java.util.Optional.of("Foo")).isPresent());
    assertEquals(
        com.google.common.base.Optional.of("Foo"),
        Optionals.toGuavaOptional(java.util.Optional.of("Foo"))
    );
  }
}
