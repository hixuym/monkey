package io.sunflower.undertow.handler;


import io.undertow.server.HttpHandler;

/**
 * A interface for middleware handlers. All middleware handlers must implement this interface
 * so that the handler can be plugged in to the request/response chain during server startup
 * with SPI (Service Provider Interface). The entire sunflower framework is a core server that
 * provides a plugin structure to hookup all sorts of plugins to handler different cross-cutting
 * concerns.
 *
 * The middleware handlers are designed based on chain of responsibility pattern.
 *
 * @author michael
 */
public interface MiddlewareHandler extends HttpHandler {

    /**
     * Get the next handler in the chain
     *
     * @return HttpHandler
     */
    HttpHandler getNext();

    /**
     * Set the next handler in the chain
     *
     * @param next HttpHandler
     * @return MiddlewareHandler
     */
    MiddlewareHandler setNext(final HttpHandler next);

    /**
     * Indicate if this handler is enabled or not.
     */
    boolean isEnabled();

}
