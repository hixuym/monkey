/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.undertow.handler;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.base.Strings;
import io.sunflower.setup.Environment;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static io.sunflower.undertow.handler.Handlers.param;

/**
 * @author michael
 */
@Singleton
public class MetricsDisplayHandler implements HttpHandler {

    private static final String CONTENT_TYPE = "application/json";

    private static final String RATE_UNIT = "rateUnit";
    private static final String DURATION_UNIT = "durationUnit";
    private static final String SHOW_SAMPLES = "showSamples";
    private static final String ALLOWED_ORIGIN = "allowedOrigin";

    private static final String jsonpParamName = "jsonpCallback";

    private final Environment environment;

    private transient MetricRegistry registry;

    @Inject
    public MetricsDisplayHandler(Environment environment) {
        this.environment = environment;
        this.registry = environment.metrics();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String allowedOrigin = param(exchange, ALLOWED_ORIGIN);

        if (allowedOrigin != null) {
            exchange.getResponseHeaders()
                    .put(new HttpString("Access-Control-Allow-Origin"), allowedOrigin);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, CONTENT_TYPE);
        exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, "must-revalidate,no-cache,no-store");

        final TimeUnit rateUnit = parseTimeUnit(param(exchange, RATE_UNIT), TimeUnit.SECONDS);
        final TimeUnit durationUnit = parseTimeUnit(param(exchange, DURATION_UNIT), TimeUnit.SECONDS);
        final boolean showSamples = Boolean.parseBoolean(param(exchange, SHOW_SAMPLES));

        ObjectMapper mapper = environment.getObjectMapper()
                .registerModule(new MetricsModule(rateUnit, durationUnit, showSamples));

        exchange.setStatusCode(StatusCodes.OK);

        String jsonp = param(exchange, jsonpParamName);

        if (Strings.isNullOrEmpty(jsonp)) {
            exchange.getResponseSender()
                    .send(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(registry));
        } else {
            exchange.getResponseSender()
                    .send(mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(new JSONPObject(jsonp, registry)));
        }
    }

    private TimeUnit parseTimeUnit(String value, TimeUnit defaultValue) {
        try {
            return TimeUnit.valueOf(String.valueOf(value).toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
