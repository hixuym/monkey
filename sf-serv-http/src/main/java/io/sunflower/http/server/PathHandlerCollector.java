package io.sunflower.http.server;

import io.sunflower.SunflowerException;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * author michael
 */
public class PathHandlerCollector {

    private static Logger logger = LoggerFactory.getLogger(PathHandlerCollector.class);

    private Map<String, HttpHandler> handlerMap;

    private volatile HttpHandler defaultHandler;

    @Inject
    public PathHandlerCollector(Map<String, HttpHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public HttpHandler buildApplicationHandler() {

        if (defaultHandler == null && handlerMap.isEmpty()) {
            throw new SunflowerException("no http handler found, please add your handlers by PathHandlerCollector.");
        }

        if (defaultHandler == null) {
            logger.warn("http server have no handler for /.");
        }

        PathHandler handler = new PathHandler(50);

        if (defaultHandler != null) {
            handler.addPrefixPath("/", defaultHandler);
        }

        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            handler.addPrefixPath(entry.getKey(), entry.getValue());
        }

        return handler;
    }


}
