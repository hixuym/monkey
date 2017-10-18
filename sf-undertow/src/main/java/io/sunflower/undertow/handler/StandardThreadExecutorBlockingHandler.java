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

import io.sunflower.lifecycle.setup.StandardThreadExecutor;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * StandardThreadExecutorBlockingHandler
 *
 * @author michael created on 17/10/18 16:31
 */
public class StandardThreadExecutorBlockingHandler implements HttpHandler {

  private final StandardThreadExecutor executor;
  private final HttpHandler handler;

  public StandardThreadExecutorBlockingHandler(
      StandardThreadExecutor executor, HttpHandler handler) {
    this.executor = executor;
    this.handler = handler;
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    exchange.startBlocking();
    if (exchange.isInIoThread()) {
      exchange.dispatch(executor, handler);
    } else {
      handler.handleRequest(exchange);
    }
  }
}