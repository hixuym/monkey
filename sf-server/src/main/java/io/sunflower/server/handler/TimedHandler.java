package io.sunflower.server.handler;

import com.codahale.metrics.Timer;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Created by michael on 17/9/1.
 */
public class TimedHandler implements HttpHandler {

    private final HttpHandler next;
    private final Timer timer;

    public TimedHandler(HttpHandler next, Timer timer) {
        this.next = next;
        this.timer = timer;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final Timer.Context context = timer.time();
        try {
            next.handleRequest(exchange);
        } finally {
            context.stop();
        }
    }
}
