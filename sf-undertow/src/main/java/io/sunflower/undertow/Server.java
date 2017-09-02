package io.sunflower.undertow;

import java.util.List;

import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.undertow.Undertow;

/**
 * Created by michael on 17/9/1.
 */
public class Server extends ContainerLifeCycle {

    private final Undertow undertow;

    public Server(Undertow undertow) {
        this.undertow = undertow;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        undertow.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        undertow.stop();
    }

    public List<Undertow.ListenerInfo> getListenerInfo() {
        return undertow.getListenerInfo();
    }

    public void addServerLifeCycleListener(ServerLifeCycleListener lifeCycleListener) {
        addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarted(LifeCycle event) {
                if (event instanceof Server) {
                    lifeCycleListener.started((Server) event);
                }
            }
        });
    }

}
