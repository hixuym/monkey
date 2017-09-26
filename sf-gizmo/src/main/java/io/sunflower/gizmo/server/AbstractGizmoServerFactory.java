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
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import io.sunflower.gizmo.Gizmo;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.template.TemplateEngine;
import io.sunflower.gizmo.utils.GizmoConstant;
import io.sunflower.guicey.Injectors;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.GarbageCollectionTask;
import io.sunflower.undertow.handler.HealthChecksHandler;
import io.sunflower.undertow.handler.LogConfigurationTask;
import io.sunflower.undertow.handler.Task;
import io.sunflower.undertow.handler.TaskHandler;
import io.undertow.Handlers;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;

import static com.google.common.base.MoreObjects.firstNonNull;

public abstract class AbstractGizmoServerFactory extends GizmoConfiguration implements GizmoServerFactory {

    private final TaskHandler taskHandler = new TaskHandler();
    private final PathHandler adminHandlers = new PathHandler();

    @JsonIgnore
    @Override
    public void addAdminHandler(String path, HttpHandler handler) {
        adminHandlers.addPrefixPath(path, handler);
    }

    @JsonIgnore
    protected HttpHandler createAdminHandler() {
        return adminHandlers;
    }

    @Override
    public final GizmoServer build(Environment environment) {
        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                logTasks();
            }
        });

        taskHandler.add(new GarbageCollectionTask());
        taskHandler.add(new LogConfigurationTask());

        Injector injector = environment.guicey().injector();

        Injectors.instanceOf(injector, Task.class).forEach(taskHandler::add);

        adminHandlers.addPrefixPath("tasks", taskHandler);
        adminHandlers.addPrefixPath("healthcheck", new HealthChecksHandler(environment));

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
        // root handler for ninja app
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

        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset(GizmoConstant.UTF_8);
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);

        // then requests MUST be blocking for IO to function
        h = new BlockingHandler(h);

        return h;
    }

    @JsonIgnore
    protected HttpHandler addAccessLogWrapper(Environment environment, HttpHandler httpHandler) {
        String format = getAccessLogFormat();

        if (StringUtils.isNotEmpty(format)) {

            ExecutorService executorService = environment.lifecycle().executorService("AccessLog-pool-%d")
                .maxThreads(1)
                .minThreads(1)
                .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
                .rejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
                .build();

            Path log = Paths.get(getAccessLogPath());

            if (!log.toFile().exists()) {
                boolean r = log.toFile().mkdirs();
            }

            AccessLogReceiver receiver = DefaultAccessLogReceiver.builder()
                .setLogBaseName("access")
                .setLogWriteExecutor(executorService)
                .setOutputDirectory(log)
                .setRotate(isAccessLogRotate())
                .build();

            return new AccessLogHandler(httpHandler, receiver, format, environment.classLoader());
        }

        return httpHandler;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Gizmo.class);

    protected void logTasks() {
        final StringBuilder stringBuilder = new StringBuilder(1024).append(String.format("%n%n"));

        for (Task task : this.taskHandler.getTasks()) {
            final String taskClassName = firstNonNull(task.getClass().getCanonicalName(), task.getClass().getName());
            stringBuilder.append(String.format("    %-7s /tasks/%s (%s)%n",
                "POST",
                task.getName(),
                taskClassName));
        }

        LOGGER.info("tasks = {}", stringBuilder.toString());
    }

}
