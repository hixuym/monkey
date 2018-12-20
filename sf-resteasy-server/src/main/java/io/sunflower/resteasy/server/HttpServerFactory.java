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

package io.sunflower.resteasy.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.resteasy.AbstractResteasyServerFactory;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * HttpServerFactory
 *
 * @author michael
 * created on 17/11/6 15:52
 */
@JsonTypeName("resteasy")
public class HttpServerFactory extends AbstractResteasyServerFactory {

    private String hostname;
    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;
    private int maxRequestSize = 1024 * 1024 * 10;
    private int maxInitialLineLength = 4096;
    private int maxHeaderSize = 8192;
    private int maxChunkSize = 8192;
    private int backlog = 128;

    /**
     * default no timeout, SECONDS
     */
    private int idleTimeout = -1;

    @Override
    public Server buildServer(Environment environment) {

        NettyJaxrsServer jaxrsServer = new NettyJaxrsServer();

        jaxrsServer.setDeployment(getDeployment());
        jaxrsServer.setPort(getPort());
        jaxrsServer.setRootResourcePath(getApplicationContextPath());
        jaxrsServer.setSecurityDomain(null);

        buildNettyServer(jaxrsServer, environment);

        return buildNettyServer(jaxrsServer, environment);
    }

    @Override
    protected void configure(ResteasyDeployment deployment) {
        super.configure(deployment);
    }

    /**
     * buildNettyServer server server
     *
     * @param server
     */
    protected Server buildNettyServer(NettyJaxrsServer server, Environment environment) {
        ResteasyServer resteasyServer = new ResteasyServer(server, environment);
        resteasyServer.setApplicationContextPath(getApplicationContextPath());
        return resteasyServer;
    }

    @JsonProperty
    public String getHostname() {
        return hostname;
    }

    @JsonProperty
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty
    public int getIoWorkerCount() {
        return ioWorkerCount;
    }

    @JsonProperty
    public void setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
    }

    @JsonProperty
    public int getExecutorThreadCount() {
        return executorThreadCount;
    }

    @JsonProperty
    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    @JsonProperty
    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    @JsonProperty
    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    @JsonProperty
    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    @JsonProperty
    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    @JsonProperty
    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    @JsonProperty
    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    @JsonProperty
    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    @JsonProperty
    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    @JsonProperty
    public int getBacklog() {
        return backlog;
    }

    @JsonProperty
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    @JsonProperty
    public int getIdleTimeout() {
        return idleTimeout;
    }

    @JsonProperty
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
