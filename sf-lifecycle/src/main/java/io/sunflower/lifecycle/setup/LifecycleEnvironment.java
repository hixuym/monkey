package io.sunflower.lifecycle.setup;

import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import static java.util.Objects.requireNonNull;

public class LifecycleEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleEnvironment.class);

    private final List<LifeCycle> managedObjects;
    private final List<LifeCycle.Listener> lifecycleListeners;

    public LifecycleEnvironment() {
        this.managedObjects = new ArrayList<>();
        this.lifecycleListeners = new ArrayList<>();
    }

    public List<LifeCycle> getManagedObjects() {
        return managedObjects;
    }

    /**
     * Adds the given {@link Managed} instance to the set of objects managed by the server's
     * lifecycle. When the server starts, {@code managed} will be started. When the server stops,
     * {@code managed} will be stopped.
     *
     * @param managed a managed object
     */
    public void manage(Managed managed) {
        managedObjects.add(new AbstractLifeCycle() {
            @Override
            protected void doStart() throws Exception {
                requireNonNull(managed).start();
            }

            @Override
            protected void doStop() throws Exception {
                requireNonNull(managed).stop();
            }

            @Override
            public String toString() {
                return requireNonNull(managed).toString();
            }
        });
    }

    /**
     * Adds the given Jetty {@link LifeCycle} instances to the server's lifecycle.
     *
     * @param managed a Jetty-managed object
     */
    public void manage(LifeCycle managed) {
        managedObjects.add(requireNonNull(managed));
    }

    public StandardThreadExecutorBuilder standardThreadExecutor(String nameFormat) {
        return new StandardThreadExecutorBuilder(this, nameFormat);
    }

    public StandardThreadExecutorBuilder standardThreadExecutor(String nameFormat, ThreadFactory factory) {
        return new StandardThreadExecutorBuilder(this, nameFormat);
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

    public ScheduledExecutorServiceBuilder scheduledExecutorService(String nameFormat,
                                                                    ThreadFactory factory) {
        return new ScheduledExecutorServiceBuilder(this, nameFormat, factory);
    }

    public ScheduledExecutorServiceBuilder scheduledExecutorService(String nameFormat,
                                                                    boolean useDaemonThreads) {
        return new ScheduledExecutorServiceBuilder(this, nameFormat, useDaemonThreads);
    }

    public void addLifeCycleListener(LifeCycle.Listener listener) {
        lifecycleListeners.add(listener);
    }

    public void attach(ContainerLifeCycle container) {
        for (LifeCycle object : managedObjects) {
            container.addBean(object);
        }
        container.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                LOGGER.debug("managed objects = {}", managedObjects);
            }
        });
        for (LifeCycle.Listener listener : lifecycleListeners) {
            container.addLifeCycleListener(listener);
        }
    }
}
