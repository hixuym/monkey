package io.sunflower.validation.valuehandling;

import java.lang.reflect.Type;
import java.util.OptionalDouble;

import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

/**
 * A {@link ValidatedValueUnwrapper} for {@link OptionalDouble}.
 *
 * Extracts the value contained by the {@link OptionalDouble} for validation, or produces {@code
 * null}.
 */
public class OptionalDoubleValidatedValueUnwrapper extends ValidatedValueUnwrapper<OptionalDouble> {

  @Override
  public Object handleValidatedValue(final OptionalDouble optional) {
    return optional.isPresent() ? optional.getAsDouble() : null;
  }

  @Override
  public Type getValidatedValueType(final Type type) {
    return Double.class;
  }
}
