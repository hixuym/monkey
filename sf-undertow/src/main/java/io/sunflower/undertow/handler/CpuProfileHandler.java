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

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CpuProfileHandler implements HttpHandler {

  private static final String CONTENT_TYPE = "pprof/raw";
  private static final String CACHE_CONTROL = "Cache-Control";
  private static final String NO_CACHE = "must-revalidate,no-cache,no-store";
  private final Lock lock = new ReentrantLock();

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    int duration = 10;
    String dura = Handlers.param(exchange, "duration");

    if (dura != null) {
      try {
        duration = Integer.parseInt(dura);
      } catch (NumberFormatException e) {
        duration = 10;
      }
    }

    int frequency = 100;
    String freq = Handlers.param(exchange, "frequency");

    if (freq != null) {
      try {
        frequency = Integer.parseInt(freq);
      } catch (NumberFormatException e) {
        frequency = 100;
      }
    }

    final Thread.State state;
    String s = Handlers.param(exchange, "state");

    if ("blocked".equalsIgnoreCase(s)) {
      state = Thread.State.BLOCKED;
    } else {
      state = Thread.State.RUNNABLE;
    }

    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, CONTENT_TYPE);
    exchange.getResponseHeaders().put(Headers.CACHE_CONTROL, NO_CACHE);
    exchange.setStatusCode(StatusCodes.OK);

    try (OutputStream output = exchange.getOutputStream()) {
      doProfile(output, duration, frequency, state);
    }
  }

  protected void doProfile(OutputStream out, int duration, int frequency, Thread.State state)
      throws IOException {
    if (lock.tryLock()) {
      try {
//                CpuProfile profile = CpuProfile.record(Duration.standardSeconds(duration),
//                    frequency, state);
//                if (profile == null) {
//                    throw new RuntimeException("could not create CpuProfile");
//                }
//                profile.writeGoogleProfile(out);
//                return;
      } finally {
        lock.unlock();
      }
    }
    throw new RuntimeException("Only one profile request may be active at a time");
  }
}
