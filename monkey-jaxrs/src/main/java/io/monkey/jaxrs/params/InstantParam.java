package io.monkey.jaxrs.params;

import javax.annotation.Nullable;
import java.time.Instant;

/**
 * A parameter encapsulating date/time values. All non-parsable values will return a {@code 400 Bad Request} response.
 */
public class InstantParam extends AbstractParam<Instant> {
    public InstantParam(@Nullable final String input) {
        super(input);
    }

    public InstantParam(@Nullable final String input, final String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected String errorMessage(Exception e) {
        return "%s must be in a ISO-8601 format.";
    }

    @Override
    protected Instant parse(@Nullable final String input) {
        return Instant.parse(input);
    }
}