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

package io.monkey.undertow.setup;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import io.monkey.inject.advise.Advises;
import io.monkey.inject.advise.ProvidesWithAdvice;
import io.monkey.undertow.HttpServerFactory;
import io.monkey.undertow.handler.SlowRequestLogHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.function.UnaryOperator;

/**
 * @author Michael
 * Created at: 2019/1/17 11:14
 */
public class UndertowModule extends AbstractModule {

    private final static Logger logger = LoggerFactory.getLogger(UndertowModule.class);

    private final HttpServerFactory serverFactory;

    public UndertowModule(HttpServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    @ProvidesWithAdvice
    @Singleton
    PathHandler getPathHandler() {
        return new PathHandler();
    }

    @Advises
    UnaryOperator<PathHandler> setupResourceHandler() {
        return (handler) -> {
            if (serverFactory.getAssetsConfig() != null) {
                HttpHandler resourceHandler = new ResourceHandler.Builder()
                    .build(ImmutableMap.of("location", serverFactory.getAssetsConfig().getResourcePath(),
                        "allow-listing", serverFactory.getAssetsConfig().isAllowListing())).wrap(null);

                handler.addPrefixPath(serverFactory.getAssetsConfig().getUriPath(), resourceHandler);
                logger.info("setup assets http handler at {}, allow-listing={}.",
                    serverFactory.getAssetsConfig().getUriPath(),
                    serverFactory.getAssetsConfig().isAllowListing());
            }
            return handler;
        };
    }

    @ProvidesWithAdvice
    @Singleton
    HttpHandler handlerChain(PathHandler pathHandler) {
        return pathHandler;
    }

    @Advises
    UnaryOperator<HttpHandler> defaultHandlerChain() {
        return handler -> {
            if (!Strings.isNullOrEmpty(serverFactory.getAccessLog())) {
                AccessLogHandler.Builder builder = new AccessLogHandler.Builder();
                handler = builder.build(ImmutableMap.of("format", serverFactory.getAccessLog())).wrap(handler);
            }

            if (serverFactory.isDumpRequest()) {
                handler = new RequestDumpingHandler(handler);
            }

            if (serverFactory.getMaxConcurrentRequests() > 0) {
                handler = new RequestLimitingHandler(serverFactory.getMaxConcurrentRequests(),
                    serverFactory.getMaxRequestsQueue(), handler);
            }

            if (serverFactory.getSlowThreshold() != null) {
                handler = new SlowRequestLogHandler(serverFactory.getSlowThreshold(), handler);
            }

            return handler;
        };
    }

}
