package io.sunflower.lifecycle.setup;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import io.sunflower.lifecycle.Managed;
import io.sunflower.lifecycle.LifecycleListener;

import static java.util.Objects.requireNonNull;

public class LifecycleEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleEnvironment.class);

    private final List<Service> managedObjects;

    private final List<LifecycleListener> lifecycleListeners;

    public LifecycleEnvironment() {
        this.managedObjects = new ArrayList<>();
        this.lifecycleListeners = new ArrayList<>();
    }

    public List<Service> getManagedObjects() {
        return managedObjects;
    }

    public List<LifecycleListener> getLifecycleListeners() {
        return lifecycleListeners;
    }

    /**
     * Adds the given {@link Managed} instance to the set of objects managed by the server's
     * lifecycle. When the server starts, {@code managed} will be started. When the server stops,
     * {@code managed} will be stopped.
     *
     * @param managed a managed object
     */
    public void manage(Managed managed) {
        Managed m = requireNonNull(managed);
        managedObjects.add(new AbstractIdleService() {
            @Override
            protected void startUp() throws Exception {
                m.start();
            }

            @Override
            protected void shutDown() throws Exception {
                m.stop();
            }
        });
    }

    /**
     * Adds the given Jetty {@link Service} instances to the server's lifecycle.
     *
     * @param managed a Jetty-managed object
     */
    public void manage(Service managed) {
        managedObjects.add(requireNonNull(managed));
    }

    public ExecutorServiceBuilder executorService(String nameFormat) {
        return new ExecutorServiceBuilder(this, nameFormat);
    }

    public ExecutorServiceBuilder executorService(String nameFormat, ThreadFactory factory) {
        return new ExecutorServiceBuilder(this, nameFormat, factory);
    }

    public ScheduledExecutorServiceBuilder scheduledExecutorService(String nameFormat) {
        return scheduledExecutorService(nameFormat, false);
    }

    public ScheduledExecutorServiceBuilder scheduledExecutorService(String nameFormat, ThreadFactory factory) {
        return new ScheduledExecutorServiceBuilder(this, nameFormat, factory);
    }

    public ScheduledExecutorServiceBuilder scheduledExecutorService(String nameFormat, boolean useDaemonThreads) {
        return new ScheduledExecutorServiceBuilder(this, nameFormat, useDaemonThreads);
    }

    public void addListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

}
