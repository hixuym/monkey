/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.undertow.handler;

import com.google.common.base.Strings;
import io.monkey.util.Duration;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class SlowRequestLogHandler implements HttpHandler {

    private final long threshold;

    private Supplier<Long> currentTimeProvider = System::nanoTime;
    private Logger logger = LoggerFactory.getLogger(SlowRequestLogHandler.class);

    private final HttpHandler next;

    private static final String RUNTIME_HEADER = "X-Runtime";

    /**
     * Creates a filter which logs requests which take longer than 1 second.
     */
    public SlowRequestLogHandler(HttpHandler next) {
        this(Duration.seconds(1), next);
    }

    /**
     * Creates a filter which logs requests which take longer than the given duration.
     *
     * @param threshold    the threshold for considering a request slow
     */
    public SlowRequestLogHandler(Duration threshold, HttpHandler next) {
        this.threshold = threshold.toNanoseconds();
        this.next = next;
    }

    private static final float NANOS_IN_SECOND = Duration.seconds(1).toNanoseconds();

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        final long startTime = currentTimeProvider.get();

        try {
            next.handleRequest(exchange);
        } finally {
            final long elapsedNS = currentTimeProvider.get() - startTime;
            final long elapsedMS = NANOSECONDS.toMillis(elapsedNS);

            final float seconds = (currentTimeProvider.get() - startTime) / NANOS_IN_SECOND;

            exchange.getResponseHeaders().put(new HttpString(RUNTIME_HEADER), String.format(Locale.ROOT, "%.6f", seconds));

            if (elapsedNS >= threshold) {
                logger.warn("Slow request: {} {} ({}ms)",
                        exchange.getRequestMethod(), fullPath(exchange), elapsedMS);
            }
        }
    }

    private String fullPath(HttpServerExchange exchange) {
        String uri = exchange.getRequestURI();

        String queryString = exchange.getQueryString();

        return Strings.isNullOrEmpty(queryString) ? uri : uri + "?" + queryString;
    }
}
