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

package io.sunflower.jaxrs.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Binding;
import com.google.inject.Injector;
import io.sunflower.jaxrs.server.setup.GuiceResourceFactory;
import io.sunflower.jaxrs.server.setup.RequestScopeModule;
import io.sunflower.jaxrs.server.setup.ResteasyModule;
import io.sunflower.jaxrs.validation.Validators;
import io.sunflower.server.AbstractServerFactory;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.util.Size;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.GetRestful;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JaxrsServerFactory
 *
 * @author michael
 * created on 17/11/10 15:53
 */
public abstract class JaxrsServerFactory extends AbstractServerFactory {

    private final ResteasyDeployment deployment = new ResteasyDeployment();

    @JsonProperty
    private String hostname;

    @JsonProperty
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;

    @JsonProperty
    private int executorThreadCount = 16;

    @JsonProperty
    private Size maxRequestSize = Size.megabytes(10);

    @JsonProperty
    private int maxInitialLineLength = 4096;

    @JsonProperty
    private int maxHeaderSize = 8192;

    @JsonProperty
    private int maxChunkSize = 8192;

    @JsonProperty
    private int backlog = 128;

    /**
     * default no timeout, SECONDS
     */
    @JsonProperty
    private int idleTimeout = -1;

    @JsonProperty("contextPath")
    private String applicationContextPath = "/";

    @JsonProperty
    private int port = 8080;

    @Override
    public final Server build(Environment environment) {

        environment.addServerLifecycleListener((server -> scanResources(server.getInjector())));

        NettyJaxrsServer nettyServer = new NettyJaxrsServer();

        nettyServer.setDeployment(deployment);
        nettyServer.setPort(port);
        nettyServer.setRootResourcePath(applicationContextPath);
        nettyServer.setSecurityDomain(null);
        nettyServer.setBacklog(backlog);
        nettyServer.setHostname(hostname);
        nettyServer.setIdleTimeout(idleTimeout);
        nettyServer.setIoWorkerCount(ioWorkerCount);
        nettyServer.setMaxChunkSize(maxChunkSize);
        nettyServer.setMaxHeaderSize(maxHeaderSize);
        nettyServer.setMaxInitialLineLength(maxInitialLineLength);
        nettyServer.setMaxRequestSize((int) maxRequestSize.toBytes());
        nettyServer.setExecutorThreadCount(executorThreadCount);

        return buildServer(nettyServer, environment);
    }

    private void scanResources(Injector injector) {
        List<Binding<?>> rootResourceBindings = new ArrayList<>();
        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getRawType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;
                if (GetRestful.isRootResource(beanClass)) {
                    // deferred registration
                    rootResourceBindings.add(binding);
                }

                if (beanClass.isAnnotationPresent(Provider.class)) {
                    logger.info("registering provider instance for {}", beanClass.getName());
                    deployment.getProviderFactory().registerProviderInstance(binding.getProvider().get());
                }
            }
        }

        for (Binding<?> binding : rootResourceBindings) {

            Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();
            final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
            logger.info("registering factory for {}", beanClass.getName());
            deployment.getRegistry().addResourceFactory(resourceFactory);

        }
    }

    /**
     * build the real server
     *
     * @param environment
     * @return
     */
    protected abstract Server buildServer(NettyJaxrsServer nettyJaxrsServer, Environment environment);

    protected void configure(ResteasyDeployment deployment) {
    }

    @Override
    public void configure(Environment environment) {
        environment.setValidatorFactory(Validators.newValidatorFactory());
        environment.guice().register(new ResteasyModule(), new RequestScopeModule());
    }

    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    public void setApplicationContextPath(String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getIoWorkerCount() {
        return ioWorkerCount;
    }

    public void setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
    }

    public int getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    public Size getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(Size maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
