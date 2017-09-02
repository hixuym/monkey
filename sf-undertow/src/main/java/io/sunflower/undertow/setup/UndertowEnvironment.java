package io.sunflower.undertow.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;

/**
 * Created by michael on 17/9/1.
 */
public class UndertowEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowEnvironment.class);

    private final PathHandler handlerContext;

    public UndertowEnvironment(PathHandler handlerContext) {
        this.handlerContext = handlerContext;
    }

    public void addUndertowHandler(String path, HttpHandler httpHandler) {
        this.handlerContext.addPrefixPath(path, httpHandler);
    }

}
