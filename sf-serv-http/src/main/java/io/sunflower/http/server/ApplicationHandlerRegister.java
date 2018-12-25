package io.sunflower.http.server;

import com.google.common.collect.Maps;
import io.sunflower.SunflowerException;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * author michael
 */
public class ApplicationHandlerRegister {

    private static Logger logger = LoggerFactory.getLogger(ApplicationHandlerRegister.class);

    private Map<String, HttpHandler> handlerMap = Maps.newConcurrentMap();

    private volatile HttpHandler defaultHandler;

    public void registry(String path, HttpHandler handler) {
        logger.info("registry http hander: {} -> {}.", path, handler);

        if ("/".equalsIgnoreCase(path)) {
            this.defaultHandler = handler;
        } else {
            this.handlerMap.put(path, handler);
        }
    }

    public HttpHandler buildApplicationHandler() {

        if (defaultHandler == null && handlerMap.isEmpty()) {
            throw new SunflowerException("no http handler found, please registry your handlers by ApplicationHandlerRegister.");
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
