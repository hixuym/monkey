package io.sunflower.lifecycle;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import java.util.concurrent.TimeUnit;

import io.sunflower.lifecycle.setup.LifecycleEnvironment;

/**
 * Created by michael on 17/9/1.
 */
public class LifecycleManager {

    private final ServiceManager serviceManager;

    public LifecycleManager(LifecycleEnvironment environment) {
        this.serviceManager = new ServiceManager(environment.getManagedObjects());

        for (LifecycleListener lifecycleListener : environment.getLifecycleListeners()) {
            serviceManager.addListener(new ServiceManager.Listener() {
                @Override
                public void healthy() {
                    lifecycleListener.started();
                }

                @Override
                public void stopped() {
                    lifecycleListener.stopped();
                }

                @Override
                public void failure(Service service) {
                    lifecycleListener.failured(service);
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.stop();
            } catch (Exception e) {
                System.exit(1);
            }
        }));
    }

    public void awaitHealthy() {
        this.serviceManager.awaitHealthy();
    }

    public void startAsync() {
        this.serviceManager.startAsync();
    }

    public void start() throws Exception {
        this.serviceManager.startAsync().awaitHealthy(5, TimeUnit.SECONDS);
    }

    public void stop() throws Exception {
        this.serviceManager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
    }

}
