package io.sunflower.server;

import com.google.common.util.concurrent.AbstractIdleService;

import io.undertow.Undertow;

/**
 * Created by michael on 17/9/1.
 */
public class Server extends AbstractIdleService {

    private final Undertow undertow;

    public Server(Undertow undertow) {
        this.undertow = undertow;
    }

    @Override
    protected void startUp() throws Exception {
        undertow.start();
    }

    @Override
    protected void shutDown() throws Exception {
        undertow.stop();
    }
}
