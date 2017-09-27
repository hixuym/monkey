/*
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sunflower.gizmo.server;

import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Gizmo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Handles a request from Undertow and then delegates to sf.
 */
public class GizmoHttpHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(GizmoHttpHandler.class);

    private Injector injector;
    private String context;
    private Gizmo gizmo;

    public void init(Injector injector, String context) {
        this.gizmo = injector.getInstance(Gizmo.class);
        this.injector = injector;
        this.context = context;
    }

    public Injector getInjector() {
        return injector;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // create Ninja compatible context element
        UndertowContext undertowContext
            = (UndertowContext) injector.getProvider(Context.class).get();

        // initialize it
        undertowContext.init(exchange, context);

        // invoke gizmo on it
        gizmo.onRouteRequest(undertowContext);
    }

}
