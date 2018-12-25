package io.sunflower.http.server;

import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        logger.info("Started HTTP Server({})", version);
    }

    @Override
    protected void shutdown() {
        if (undertow != null && started) {
            undertow.stop();
            logger.info("Stopped HTTP Server({})", version);
        }
    }

}
