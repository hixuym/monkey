package io.sunflower.validation.valuehandling;

import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

import java.lang.reflect.Type;
import java.util.OptionalInt;

/**
 * A {@link ValidatedValueUnwrapper} for {@link OptionalInt}.
 * <p>
 * Extracts the value contained by the {@link OptionalInt} for validation, or produces {@code
 * null}.
 *
 * @author michael
 */
public class OptionalIntValidatedValueUnwrapper extends ValidatedValueUnwrapper<OptionalInt> {

    @Override
    public Object handleValidatedValue(final OptionalInt optional) {
        return optional.isPresent() ? optional.getAsInt() : null;
    }

    @Override
    public Type getValidatedValueType(final Type type) {
        return Integer.class;
    }
}
