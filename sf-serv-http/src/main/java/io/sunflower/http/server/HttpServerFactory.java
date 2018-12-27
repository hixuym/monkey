package io.sunflower.http.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Environment;
import io.sunflower.util.Size;
import io.sunflower.validation.PortRange;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
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

    @Override
    public Server build(Environment environment) {

        Undertow.ListenerBuilder builder = getListener(environment);

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
        PathHandlerCollector collector = environment.getInjector().getInstance(PathHandlerCollector.class);
        builder.setHost(host);
        builder.setPort(port);
        builder.setRootHandler(collector.buildApplicationHandler());
        return builder;
    }

    @Override
    public void configure(Environment environment) {
        environment.guice().register(PathHandlerCollector.class);
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
}
