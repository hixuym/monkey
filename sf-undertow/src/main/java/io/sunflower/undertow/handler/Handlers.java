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

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;

import java.util.Deque;

/**
 * @author michael
 */
public abstract class Handlers {

    public static HandlerWrapper BLOCKING_WRAPPER = handler -> {
        HttpHandler h = handler;
        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset("utf-8");
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);
        // then requests MUST be blocking for IO to function
        return new BlockingHandler(h);
    };

    public static String param(HttpServerExchange exchange, String name) {
        Deque<String> vals = exchange.getQueryParameters().get(name);
        if (vals == null || vals.isEmpty()) {
            return null;
        }
        return vals.getFirst();
    }

}
