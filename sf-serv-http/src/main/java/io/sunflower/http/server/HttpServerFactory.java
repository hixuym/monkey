package io.sunflower.http.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.sunflower.server.Server;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Environment;
import io.sunflower.util.Duration;
import io.sunflower.util.Size;
import io.sunflower.validation.PortRange;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Undertow.ListenerBuilder builder = new Undertow.ListenerBuilder();
        builder.setHost(host);
        builder.setPort(port);
        builder.setRootHandler(getRootHandler(environment));
        return builder;
    }

    protected HttpHandler getRootHandler(Environment environment) {

        PathHandlerCollector collector = environment.getInjector().getInstance(PathHandlerCollector.class);

        HttpHandler rootHandler = collector.buildApplicationHandler();

        if (!Strings.isNullOrEmpty(accessLog)) {
            AccessLogHandler.Builder builder = new AccessLogHandler.Builder();
            rootHandler = builder.build(ImmutableMap.of("format", accessLog)).wrap(rootHandler);
        }

        if (isDumpRequest()) {
            rootHandler = new RequestDumpingHandler(rootHandler);
        }

        if (maxConcurrentRequests > 0) {
            rootHandler = new RequestLimitingHandler(maxConcurrentRequests, maxRequestsQueue, rootHandler);
        }

        if (slowThreshold != null) {
            rootHandler = new SlowRequestLogHandler(slowThreshold, rootHandler);
        }

        return rootHandler;
    }

    @Override
    public void configure(Environment environment) {
        environment.guicify().register(PathHandlerCollector.class);
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
}
