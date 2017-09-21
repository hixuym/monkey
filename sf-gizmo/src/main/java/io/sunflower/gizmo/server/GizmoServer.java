/**
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.server;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;

import io.sunflower.gizmo.Gizmo;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;
import io.sunflower.guicey.Injectors;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.handler.Task;
import io.sunflower.undertow.handler.TaskManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
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

/**
 * sunflower standalone based on Undertow.
 */
@Singleton
public class GizmoServer extends ContainerLifeCycle {

    private Logger logger = LoggerFactory.getLogger(GizmoServer.class);

    private final GizmoConfiguration configuration;
    private final Environment environment;

    private Undertow undertow;
    private boolean undertowStarted;                      // undertow fails on stop() if start() never called

    protected SSLContext sslContext;

    private Gizmo gizmo;

    private final Injector injector;

    @Inject
    public GizmoServer(Environment environment, GizmoConfiguration configuration) {
        this.configuration = configuration;
        this.environment = environment;
        this.injector = environment.guicey().injector();
    }

    public void init() {
        this.undertow = createUndertow();
        this.gizmo = injector.getInstance(Gizmo.class);
    }

    @Override
    public void doStart() throws Exception {

        super.doStart();

        this.initRoutes();

        this.gizmo.onFrameworkStart();

        String version = undertow.getClass().getPackage().getImplementationVersion();
        logger.info("Trying to start undertow v{}", version);
        this.undertow.start();
        undertowStarted = true;
        logger.info("Started undertow v{}", version);
    }

    private void initRoutes() {
        Set<ApplicationRoutes> routes = Injectors.getInstancesOf(injector, ApplicationRoutes.class);

        Router router = injector.getInstance(Router.class);

        for (ApplicationRoutes route : routes) {
            route.init(router);
        }

        router.compileRoutes();
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();

        this.gizmo.onFrameworkShutdown();

        if (this.undertow != null && undertowStarted) {
            logger.info("Trying to stop undertow.");
            this.undertow.stop();
            logger.info("Stopped undertow.");
            this.undertow = null;
        }
    }

    private Undertow createUndertow() {

        Undertow.Builder undertowBuilder = Undertow.builder()
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        logger.info("Undertow h2 protocol (undertow.http2 = {})", configuration.isHttp2Enabled());

        undertowBuilder.setServerOption(UndertowOptions.ENABLE_HTTP2, configuration.isHttp2Enabled());

        HttpHandler applicationHandler = createApplicationHandler();

        for (ConnectorFactory connectorFactory : configuration.getApplicationConnectors()) {
            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            listenerBuilder.setRootHandler(addAccessLogWrapper(applicationHandler));

            undertowBuilder.addListener(listenerBuilder);
        }

        HttpHandler adminHandler = createAdminHandler(environment);

        for (ConnectorFactory connectorFactory : configuration.getAdminConnectors()) {
            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            listenerBuilder.setRootHandler(addAccessLogWrapper(adminHandler));

            undertowBuilder.addListener(listenerBuilder);
        }

        return undertowBuilder.build();
    }

    private HttpHandler createAdminHandler(Environment environment) {

        TaskManager manager = injector.getInstance(TaskManager.class);

        Set<Task> taskSet = Injectors.getInstancesOf(injector, Task.class);

        for (Task task : taskSet) {
            manager.add(task);
        }

        PathHandler h = new PathHandler();

        h.addPrefixPath("tasks", TaskManager.createHandler(manager));

        if (!Strings.isNullOrEmpty(configuration.getAdminContextPath())) {
            h = new PathHandler().addPrefixPath(configuration.getAdminContextPath(), h);
        }

        return h;
    }

    private HttpHandler createApplicationHandler() {
        // root handler for ninja app
        GizmoHttpHandler gizmoHttpHandler = new GizmoHttpHandler();

        // slipstream injector into undertow handler BEFORE server starts
        gizmoHttpHandler.init(injector, configuration.getApplicationContextPath());

        HttpHandler h = gizmoHttpHandler;

        // wireshark enabled?
        if (configuration.isTraceEnabled()) {
            logger.info("Undertow tracing of requests and responses activated (undertow.tracing = true)");
            // only activate request dumping on non-assets
            Predicate isAssets = Predicates.prefix("/assets");
            h = Handlers.predicate(isAssets, h, new RequestDumpingHandler(h));
        }

        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset("utf-8");
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);

        // then requests MUST be blocking for IO to function
        h = new BlockingHandler(h);

        // then a context if one exists
        if (!Strings.isNullOrEmpty(configuration.getApplicationContextPath())) {
            h = new PathHandler().addPrefixPath(configuration.getApplicationContextPath(), h);
        }

        return h;
    }

    private HttpHandler addAccessLogWrapper(HttpHandler httpHandler) {
        String format = configuration.getAccessLogFormat();

        if (StringUtils.isNotEmpty(format)) {

            ExecutorService executorService = environment.lifecycle().executorService("AccessLog-pool-%d")
                .maxThreads(1)
                .minThreads(1)
                .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
                .rejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
                .build();

            Path log = Paths.get(configuration.getAccessLogPath());

            if (!log.toFile().exists()) {
                log.toFile().mkdirs();
            }

            AccessLogReceiver receiver = DefaultAccessLogReceiver.builder()
                .setLogBaseName("access")
                .setLogWriteExecutor(executorService)
                .setOutputDirectory(log)
                .setRotate(configuration.isAccessLogRotate())
                .build();

            return new AccessLogHandler(httpHandler, receiver, format, environment.classLoader());
        }

        return httpHandler;
    }
}
