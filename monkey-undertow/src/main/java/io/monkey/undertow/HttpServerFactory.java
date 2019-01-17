/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.undertow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.monkey.server.Server;
import io.monkey.server.ServerFactory;
import io.monkey.setup.Environment;
import io.monkey.undertow.setup.UndertowModule;
import io.monkey.util.Duration;
import io.monkey.util.Size;
import io.monkey.validation.PortRange;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

@JsonTypeName("http")
public class HttpServerFactory implements ServerFactory {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @JsonProperty
    private String host = "localhost";

    @JsonProperty
    @PortRange
    private int port = 8080;

    @JsonProperty
    private int ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);

    @JsonProperty
    private int workerThreads = ioThreads * 8;

    @JsonProperty
    private Size bufferSize = Size.kilobytes(16);
    @JsonProperty
    private boolean directBuffers = true;

    @JsonProperty
    private boolean enableHttp2 = false;

    @JsonProperty
    private boolean enableHttp2Push = false;

    @JsonProperty
    private Size maxHeaderSize = Size.megabytes(1);

    @JsonProperty
    private Size maxEntitySize = Size.megabytes(10);

    @JsonProperty
    private Size maxMultipartEntitySize = Size.megabytes(10);

    @JsonProperty
    private String accessLog;

    @JsonProperty
    private boolean dumpRequest = false;

    @JsonProperty
    private int maxConcurrentRequests = -1;
    @JsonProperty
    private int maxRequestsQueue = 1000;

    @JsonProperty
    private Duration slowThreshold;

    @Valid
    private AssetsConfig assetsConfig;

    @Override
    public Server build(Environment environment) {
        Undertow.ListenerBuilder builder = getListener(environment);
        builder.setType(Undertow.ListenerType.HTTP);
        Undertow undertow = Undertow.builder()
            .addListener(builder)
            .setIoThreads(ioThreads)
            .setWorkerThreads(workerThreads)
            .setDirectBuffers(directBuffers)
            .setBufferSize((int) bufferSize.toBytes() - 20)
            .setServerOption(UndertowOptions.ENABLE_HTTP2, enableHttp2)
            .setServerOption(UndertowOptions.MAX_HEADER_SIZE, (int) maxHeaderSize.toBytes())
            .setServerOption(UndertowOptions.MAX_ENTITY_SIZE, maxEntitySize.toBytes())
            .setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE, maxMultipartEntitySize.toBytes())
            .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, enableHttp2Push)
            .build();

        return new HttpServer(environment, undertow);
    }

    protected Undertow.ListenerBuilder getListener(Environment environment) {
        return new Undertow.ListenerBuilder()
            .setHost(host)
            .setPort(port)
            .setRootHandler(environment.getInjector().getInstance(HttpHandler.class));
    }

    @Override
    public void configure(Environment environment) {
        environment.guicify().register(new UndertowModule(this));
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIoThreads() {
        return ioThreads;
    }

    public void setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public Size getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Size bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isDirectBuffers() {
        return directBuffers;
    }

    public void setDirectBuffers(boolean directBuffers) {
        this.directBuffers = directBuffers;
    }

    public boolean isEnableHttp2() {
        return enableHttp2;
    }

    public void setEnableHttp2(boolean enableHttp2) {
        this.enableHttp2 = enableHttp2;
    }

    public Size getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public void setMaxHeaderSize(Size maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public boolean isEnableHttp2Push() {
        return enableHttp2Push;
    }

    public void setEnableHttp2Push(boolean enableHttp2Push) {
        this.enableHttp2Push = enableHttp2Push;
    }

    public Size getMaxEntitySize() {
        return maxEntitySize;
    }

    public void setMaxEntitySize(Size maxEntitySize) {
        this.maxEntitySize = maxEntitySize;
    }

    public Size getMaxMultipartEntitySize() {
        return maxMultipartEntitySize;
    }

    public void setMaxMultipartEntitySize(Size maxMultipartEntitySize) {
        this.maxMultipartEntitySize = maxMultipartEntitySize;
    }

    public String getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(String accessLog) {
        this.accessLog = accessLog;
    }

    public boolean isDumpRequest() {
        return dumpRequest;
    }

    public void setDumpRequest(boolean dumpRequest) {
        this.dumpRequest = dumpRequest;
    }

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public void setMaxConcurrentRequests(int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }

    public int getMaxRequestsQueue() {
        return maxRequestsQueue;
    }

    public void setMaxRequestsQueue(int maxRequestsQueue) {
        this.maxRequestsQueue = maxRequestsQueue;
    }

    public Duration getSlowThreshold() {
        return slowThreshold;
    }

    public void setSlowThreshold(Duration slowThreshold) {
        this.slowThreshold = slowThreshold;
    }

    @JsonProperty("assets")
    public AssetsConfig getAssetsConfig() {
        return assetsConfig;
    }

    @JsonProperty("assets")
    public void setAssetsConfig(AssetsConfig assetsConfig) {
        this.assetsConfig = assetsConfig;
    }

    public static class AssetsConfig {
        @NotEmpty
        private String resourcePath;

        @NotEmpty
        private String uriPath = "/assets";

        private boolean allowListing = false;

        @JsonProperty
        public String getResourcePath() {
            return resourcePath;
        }

        @JsonProperty
        public void setResourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        @JsonProperty
        public String getUriPath() {
            return uriPath;
        }

        @JsonProperty
        public void setUriPath(String uriPath) {
            this.uriPath = uriPath;
        }

        @JsonProperty
        public boolean isAllowListing() {
            return allowListing;
        }

        @JsonProperty
        public void setAllowListing(boolean allowListing) {
            this.allowListing = allowListing;
        }
    }
}
