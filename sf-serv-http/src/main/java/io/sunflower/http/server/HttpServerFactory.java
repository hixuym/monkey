package io.sunflower.http.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.validation.PortRange;
import io.undertow.Undertow;
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

    @Override
    public Server build(Environment environment) {

        ApplicationHandlerRegister register = environment.getInjector().getInstance(ApplicationHandlerRegister.class);

        Undertow.ListenerBuilder builder = new Undertow.ListenerBuilder();
        builder.setHost(host);
        builder.setPort(port);
        builder.setRootHandler(register.buildApplicationHandler());

        return buildServer(environment, builder);
    }

    protected Server buildServer(Environment environment, Undertow.ListenerBuilder builder) {
        builder.setType(Undertow.ListenerType.HTTP);
        return new HttpServer(environment, Undertow.builder().addListener(builder).build());
    }


    @Override
    public void initialize(Bootstrap bootstrap) {
        bootstrap.injectorFacotry().register(new ApplicationHandlerRegister());
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getSchema() {
        return "http";
    }
}
