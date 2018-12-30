package io.monkey.http.server;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.monkey.server.Server;
import io.monkey.setup.Environment;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpServer extends Server {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final String version;
    private boolean started;
    private Undertow undertow;

    public HttpServer(Environment environment, Undertow undertow) {
        super(environment);
        this.undertow = undertow;
        this.version = Undertow.class.getPackage().getImplementationVersion();
    }

    @Override
    protected void boot() {
        undertow.start();
        started = true;

        List<Undertow.ListenerInfo> listeners = undertow.getListenerInfo();

        List<String> strings = Lists.newArrayList();

        for (Undertow.ListenerInfo info : listeners) {
            strings.add(info.getProtcol() + "://" + info.getAddress());
        }

        logger.info("Started HTTP Server({}) With Connectors:", version);
        logger.info("   {}", Joiner.on("\n").join(strings));
    }

    @Override
    protected void shutdown() {
        if (undertow != null && started) {
            undertow.stop();
            logger.info("Stopped HTTP Server({})", version);
        }
    }

}
