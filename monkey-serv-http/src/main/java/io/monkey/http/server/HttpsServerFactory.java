package io.monkey.http.server;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.monkey.setup.Environment;
import io.monkey.ssl.SslContextFactoryFactory;
import io.undertow.Undertow;

@JsonTypeName("https")
public class HttpsServerFactory extends HttpServerFactory {

    @JsonProperty("ssl")
    private SslContextFactoryFactory sslContextFactoryFactory = new SslContextFactoryFactory();

    @Override
    protected Undertow.ListenerBuilder getListener(Environment environment) {

        // workaround for chrome issue w/ JVM and self-signed certs triggering
        // an IOException that can safely be ignored
        ch.qos.logback.classic.Logger root
                = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("io.undertow.request.io");
        root.setLevel(Level.WARN);

        Undertow.ListenerBuilder builder = super.getListener(environment);
        builder.setType(Undertow.ListenerType.HTTPS);
        builder.setSslContext(sslContextFactoryFactory.build(environment));

        return builder;
    }

    public SslContextFactoryFactory getSslContextFactoryFactory() {
        return sslContextFactoryFactory;
    }

    public void setSslContextFactoryFactory(SslContextFactoryFactory sslContextFactoryFactory) {
        this.sslContextFactoryFactory = sslContextFactoryFactory;
    }

}
