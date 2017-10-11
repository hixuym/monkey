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

import com.codahale.metrics.health.HealthCheck;
import io.sunflower.setup.Environment;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.util.Map;
import java.util.SortedMap;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HealthChecksHandler implements HttpHandler {

  private static final String CONTENT_TYPE = "application/json";
  private static final String CACHE_CONTROL = "must-revalidate,no-cache,no-store";

  private final Environment environment;

  @Inject
  public HealthChecksHandler(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {

    final SortedMap<String, HealthCheck.Result> results = environment.runHealthChecks();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, CONTENT_TYPE);
    exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, CACHE_CONTROL);

    if (results.isEmpty()) {
      exchange.setStatusCode(StatusCodes.NOT_IMPLEMENTED);
    } else {
      if (isAllHealthy(results)) {
        exchange.setStatusCode(StatusCodes.OK);
      } else {
        exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
      }
    }

    exchange.getResponseSender().send(environment.healthCheckWriter().writeValueAsString(results));
  }

  private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
    for (HealthCheck.Result result : results.values()) {
      if (!result.isHealthy()) {
        return false;
      }
    }
    return true;
  }

}
