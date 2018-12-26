package io.sunflower.http.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.ssl.SslContextFactoryFactory;
import io.undertow.Undertow;

@JsonTypeName("https")
public class HttpsServerFactory extends HttpServerFactory {

    @JsonProperty("ssl")
    private SslContextFactoryFactory sslContextFactoryFactory = new SslContextFactoryFactory();

    @Override
    protected Server buildServer(Environment environment, Undertow.ListenerBuilder builder) {
        builder.setType(Undertow.ListenerType.HTTPS);
        builder.setSslContext(sslContextFactoryFactory.build(environment));
        return new HttpServer(environment, Undertow.builder().addListener(builder).build());
    }

    public SslContextFactoryFactory getSslContextFactoryFactory() {
        return sslContextFactoryFactory;
    }

    public void setSslContextFactoryFactory(SslContextFactoryFactory sslContextFactoryFactory) {
        this.sslContextFactoryFactory = sslContextFactoryFactory;
    }

}
