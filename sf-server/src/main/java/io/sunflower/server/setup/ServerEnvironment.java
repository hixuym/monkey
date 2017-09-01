package io.sunflower.server.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

/**
 * Created by michael on 17/9/1.
 */
public class ServerEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEnvironment.class);

    private final PathHandler dispatcher = Handlers.path();

    public void addHandler(String path, HttpHandler httpHandler) {
        this.dispatcher.addPrefixPath(path, httpHandler);
    }

    public PathHandler getDispatcher() {
        return dispatcher;
    }
}
