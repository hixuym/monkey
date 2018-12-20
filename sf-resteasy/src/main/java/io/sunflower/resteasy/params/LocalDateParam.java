package io.sunflower.resteasy.params;

import org.joda.time.LocalDate;

import javax.annotation.Nullable;

/**
 * A parameter encapsulating local date values. All non-parsable values will return a {@code 400 Bad
 * Request} response.
 */
public class LocalDateParam extends AbstractParam<LocalDate> {
    public LocalDateParam(@Nullable String input) {
        super(input);
    }

    public LocalDateParam(@Nullable String input, String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected LocalDate parse(@Nullable String input) {
        return new LocalDate(input);
    }
}
