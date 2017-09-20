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
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;

/**
 * Ninja standalone based on Undertow.
 */
public class GizmoServer extends ContainerLifeCycle {

    private Logger logger = LoggerFactory.getLogger(GizmoServer.class);

    private final GizmoConfiguration configuration;
    private final Environment environment;

    protected Undertow undertow;
    protected boolean undertowStarted;                      // undertow fails on stop() if start() never called

    protected HttpHandler applicationHandler;
    private HttpHandler adminHandler;

    protected GizmoHttpHandler gizmoHttpHandler;
    protected SSLContext sslContext;

    private final Gizmo gizmo;

    private final Injector injector;

    @Inject
    public GizmoServer(Environment environment, GizmoConfiguration configuration) {
        this.configuration = configuration;
        this.environment = environment;
        this.injector = environment.guicey().injector();

        this.gizmo = injector.getInstance(Gizmo.class);
        environment.lifecycle().attach(this);
    }

    @Override
    public void doStart() throws Exception {

        this.initRoutes();

        this.gizmo.onFrameworkStart();

        this.undertow = createUndertow();
        String version = undertow.getClass().getPackage().getImplementationVersion();
        logger.info("Trying to start undertow v{} {}", version, configuration.getLoggableIdentifier());
        this.undertow.start();
        undertowStarted = true;
        logger.info("Started undertow v{} {}", version, configuration.getLoggableIdentifier());
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
    public void doStop() {
        this.gizmo.onFrameworkShutdown();

        if (this.undertow != null && undertowStarted) {
            logger.info("Trying to stop undertow {}", configuration.getLoggableIdentifier());
            this.undertow.stop();
            logger.info("Stopped undertow {}", configuration.getLoggableIdentifier());
            this.undertow = null;
        }
    }

    private Undertow createUndertow() {

        Undertow.Builder undertowBuilder = Undertow.builder()
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        logger.info("Undertow h2 protocol (undertow.http2 = {})", configuration.isHttp2Enabled());

        undertowBuilder.setServerOption(UndertowOptions.ENABLE_HTTP2, configuration.isHttp2Enabled());

        this.applicationHandler = createApplicationHandler();

        for (ConnectorFactory connectorFactory : configuration.getApplicationConnectors()) {
            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            listenerBuilder.setRootHandler(applicationHandler);

            undertowBuilder.addListener(listenerBuilder);
        }

        this.adminHandler = createAdminHandler(environment);

        for (ConnectorFactory connectorFactory : configuration.getAdminConnectors()) {
            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            listenerBuilder.setRootHandler(adminHandler);

            undertowBuilder.addListener(listenerBuilder);
        }

        return undertowBuilder.build();
    }

    // sub-classes may be interested in these
    protected HttpHandler createAdminHandler(Environment environment) {

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

    protected HttpHandler createApplicationHandler() {
        // root handler for ninja app
        this.gizmoHttpHandler = new GizmoHttpHandler();

        // slipstream injector into undertow handler BEFORE server starts
        this.gizmoHttpHandler.init(injector, configuration.getApplicationContextPath());

        HttpHandler h = this.gizmoHttpHandler;

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
}
