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

package io.sunflower.gizmo.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.guicey.Injectors;
import io.sunflower.setup.Environment;
import io.undertow.Handlers;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;

public abstract class AbstractGizmoServerFactory extends GizmoConfiguration implements GizmoServerFactory {

    private final PathHandler adminHandlers = new PathHandler();

    @JsonIgnore
    protected HttpHandler createAdminHandler() {
        return adminHandlers;
    }

    @Override
    public final GizmoServer build(Environment environment) {

        Injector injector = environment.guicey().injector();

        Injectors.mapOf(injector, new TypeLiteral<Map<String, HttpHandler>>() {})
            .forEach(adminHandlers::addPrefixPath);

        return buildServer(environment);

    }

    protected abstract GizmoServer buildServer(Environment environment);

    /**
     *
     * @param injector
     * @return
     */
    @JsonIgnore
    protected HttpHandler createApplicationHandler(Injector injector) {
        // root handler for sf app
        GizmoHttpHandler gizmoHttpHandler = new GizmoHttpHandler();

        // slipstream injector into undertow handler BEFORE server starts
        gizmoHttpHandler.init(injector, getApplicationContextPath());

        HttpHandler h = gizmoHttpHandler;

        // wireshark enabled?
        if (isTraceEnabled()) {
            logger.info("Undertow tracing of requests and responses activated (undertow.tracing = true)");
            // only activate request dumping on non-assets
            Predicate isAssets = Predicates.prefix("/assets");
            h = Handlers.predicate(isAssets, h, new RequestDumpingHandler(h));
        }

        return io.sunflower.undertow.handler.Handlers.blocking(h);
    }

    private ExecutorService accessLogExecutor = null;

    @JsonIgnore
    protected HttpHandler addAccessLogWrapper(String baseName, Environment environment, HttpHandler httpHandler) {
        String format = getAccessLogFormat();

        if (StringUtils.isNotEmpty(format)) {

            if (accessLogExecutor == null) {
                accessLogExecutor = environment.lifecycle().executorService("AccessLog-pool-%d-" + baseName)
                    .maxThreads(2)
                    .minThreads(1)
                    .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
                    .rejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
                    .build();
            }

            Path log = Paths.get(getAccessLogPath());

            if (!log.toFile().exists()) {
                boolean r = log.toFile().mkdirs();
            }

            AccessLogReceiver receiver = DefaultAccessLogReceiver.builder()
                .setLogBaseName(baseName)
                .setLogWriteExecutor(accessLogExecutor)
                .setOutputDirectory(log)
                .setRotate(isAccessLogRotate())
                .build();

            return new AccessLogHandler(httpHandler, receiver, format, environment.classLoader());
        }

        return httpHandler;
    }

}
