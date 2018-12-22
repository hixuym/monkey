package io.sunflower.jaxrs.filter;

import io.sunflower.util.Duration;

import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * This class adds an "X-Runtime" HTTP response header that includes the time
 * taken to execute the request, in seconds (based on the implementation from
 * Ruby on Rails).
 *
 * @see <a href="https://github.com/rack/rack/blob/2.0.0/lib/rack/runtime.rb">Rack::Runtime</a>
 */
@Provider
@PreMatching
public class RuntimeFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final float NANOS_IN_SECOND = Duration.seconds(1).toNanoseconds();
    private static final String RUNTIME_HEADER = "X-Runtime";
    private static final String RUNTIME_PROPERTY = "io.sunflower.jaxrs.filter.runtime";

    private Supplier<Long> currentTimeProvider = System::nanoTime;

    void setCurrentTimeProvider(Supplier<Long> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    public void filter(final ContainerRequestContext request) {
        request.setProperty(RUNTIME_PROPERTY, currentTimeProvider.get());
    }

    @Override
    public void filter(final ContainerRequestContext request,
            final ContainerResponseContext response) {

        final Long startTime = (Long) request.getProperty(RUNTIME_PROPERTY);
        if (startTime != null) {
            final float seconds = (currentTimeProvider.get() - startTime) / NANOS_IN_SECOND;
            response.getHeaders().putSingle(RUNTIME_HEADER, String.format(Locale.ROOT, "%.6f", seconds));
        }
    }
}
