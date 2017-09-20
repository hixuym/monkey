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

import com.google.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import javax.net.ssl.SSLContext;

import ch.qos.logback.classic.Level;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.setup.Environment;
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
public class GizmoUndertow extends ContainerLifeCycle {

    private Logger logger = LoggerFactory.getLogger(GizmoUndertow.class);

    private final GizmoConfiguration configuration;
    private final Environment environment;

    protected Undertow undertow;
    protected boolean undertowStarted;                      // undertow fails on stop() if start() never called
    protected HttpHandler undertowHandler;
    protected GizmoUndertowHandler gizmoUndertowHandler;
    protected SSLContext sslContext;

    @Inject
    public GizmoUndertow(Environment environment, GizmoConfiguration configuration) {

        this.configuration = configuration;
        this.environment = environment;

        environment.lifecycle().attach(this);
    }


    @Override
    public void doStart() throws Exception {

        // create chain of undertow handlers
        this.undertowHandler = createHttpHandler();

        this.undertow = createUndertow();

        // slipstream injector into undertow handler BEFORE server starts
        this.gizmoUndertowHandler.init(environment.guicy().injector(), configuration.getApplicationContextPath());

        String version = undertow.getClass().getPackage().getImplementationVersion();

        logger.info("Trying to start undertow v{} {}", version, configuration.getLoggableIdentifier());

        this.undertow.start();
        undertowStarted = true;

        logger.info("Started undertow v{} {}", version, configuration.getLoggableIdentifier());
    }

    @Override
    public void doStop() {
        if (this.undertow != null && undertowStarted) {
            logger.info("Trying to stop undertow {}", configuration.getLoggableIdentifier());
            this.undertow.stop();
            logger.info("Stopped undertow {}", configuration.getLoggableIdentifier());
            this.undertow = null;
        }
    }

    // sub-classes may be interested in these

    protected HttpHandler createHttpHandler() {
        // root handler for ninja app
        this.gizmoUndertowHandler = new GizmoUndertowHandler();

        HttpHandler h = this.gizmoUndertowHandler;

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
        if (StringUtils.isNotEmpty(configuration.getApplicationContextPath())) {
            h = new PathHandler().addPrefixPath(configuration.getApplicationContextPath(), h);
        }

        return h;
    }

    protected Undertow.Builder createUndertowBuilder() throws Exception {
        Undertow.Builder undertowBuilder = Undertow.builder()
            .setHandler(this.undertowHandler)
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        if (configuration.isPortEnabled()) {
            undertowBuilder.addHttpListener(configuration.getPort(), configuration.getHost());
        }

        if (configuration.isSslPortEnabled()) {
            this.sslContext = createSSLContext();

            // workaround for chrome issue w/ JVM and self-signed certs triggering
            // an IOException that can safely be ignored
            ch.qos.logback.classic.Logger root
                = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("io.undertow.request.io");
            root.setLevel(Level.WARN);

            undertowBuilder.addHttpsListener(configuration.getSslPort(), configuration.getHost(), this.sslContext);
        }

        logger.info("Undertow h2 protocol (undertow.http2 = {})", configuration.isHttp2Enabled());
        undertowBuilder.setServerOption(UndertowOptions.ENABLE_HTTP2, configuration.isHttp2Enabled());

        return undertowBuilder;
    }

    protected Undertow createUndertow() throws Exception {
        return createUndertowBuilder().build();
    }

    protected SSLContext createSSLContext() throws Exception {
        if (configuration.getSslKeystoreUri() == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key "
                + " has empty value.  Please check your configuration file.");
        }

        if (configuration.getSslKeystorePass() == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key "
                + " has empty value.  Please check your configuration file.");
        }

        if (configuration.getSslTruststoreUri() == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key "
                + " has empty value.  Please check your configuration file.");
        }

        if (configuration.getSslTruststorePass() == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key "
                + " has empty value.  Please check your configuration file.");
        }

        return ServerHelper.createSSLContext(new URI(configuration.getSslKeystoreUri()), configuration.getSslKeystorePass().toCharArray(),
            new URI(configuration.getSslTruststoreUri()), configuration.getSslTruststorePass().toCharArray());
    }
}
