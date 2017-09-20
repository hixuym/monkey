package io.sunflower.gizmo.server;

import com.google.inject.Injector;

import io.sunflower.lifecycle.LifecycleManager;
import io.sunflower.server.Server;

/**
 * Created by michael on 17/9/12.
 */
public class GizmoServer extends Server {

    private final Injector injector;
    private final LifecycleManager lifecycleManager;

    public GizmoServer(Injector injector) {
        this.injector = injector;
        this.lifecycleManager = injector.getInstance(LifecycleManager.class);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.lifecycleManager.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this.lifecycleManager.stop();
    }

}
