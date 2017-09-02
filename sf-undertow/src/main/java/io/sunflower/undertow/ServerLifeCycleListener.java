package io.sunflower.undertow;

import java.util.EventListener;

/**
 * Created by michael on 17/9/2.
 */
public interface ServerLifeCycleListener extends EventListener {

    void started(Server server);

    /**
     * Return the local port of the first {@link io.undertow.Undertow.ListenerInfo} in the
     * provided {@link Server} instance.
     *
     * @param server Server instance to use
     * @return First local port of the server instance
     */
    default int getLocalPort(Server server) {
        return 8080;
    }

    /**
     * Return the local port of the last {@link io.undertow.Undertow.ListenerInfo} in the
     * provided {@link Server} instance. This may be the same value as returned
     * by {@link #getLocalPort(Server)} if using the "simple" server configuration.
     *
     * @param server Server instance to use
     * @return Last local port or the server instance
     */
    default int getAdminPort(Server server) {
        return 8081;
    }
}
