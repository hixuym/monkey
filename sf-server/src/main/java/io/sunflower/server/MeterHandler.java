package io.sunflower.server;

import com.codahale.metrics.Meter;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Created by michael on 17/9/1.
 */
public class MeterHandler implements HttpHandler {

    private final HttpHandler next;
    private final Meter meter;

    public MeterHandler(HttpHandler next, Meter meter) {
        this.next = next;
        this.meter = meter;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        meter.mark();

        next.handleRequest(exchange);
    }
}
