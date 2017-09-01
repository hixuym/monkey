package io.sunflower.server.handler;

import com.codahale.metrics.Meter;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Created by michael on 17/9/1.
 */
public class ExceptionMeteredHandler implements HttpHandler {

    private final HttpHandler next;
    private final Meter exceptionMeter;
    private final Class<?> exceptionClass;

    public ExceptionMeteredHandler(HttpHandler next, Meter exceptionMeter, Class<?> exceptionClass) {
        this.next = next;
        this.exceptionMeter = exceptionMeter;
        this.exceptionClass = exceptionClass;
    }

    private boolean isReallyAssignableFrom(Exception e) {
        return exceptionClass.isAssignableFrom(e.getClass()) ||
            (e.getCause() != null && exceptionClass.isAssignableFrom(e.getCause().getClass()));
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            next.handleRequest(exchange);
        } catch (Exception e) {
            if (exceptionMeter != null && isReallyAssignableFrom(e)) {
                exceptionMeter.mark();
            } else {
                throw e;
            }
        }
    }
}
