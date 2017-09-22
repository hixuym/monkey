package io.sunflower.gizmo.server;

import net.sourceforge.argparse4j.inf.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.cli.EnvironmentCommand;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.setup.Environment;

class ServerCommand<T extends Configuration> extends EnvironmentCommand<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoServer.class);

    private final Class<T> configurationClass;

    private final GizmoBundle bundle;

    /**
     * Creates a new environment command.
     *
     * @param application the application providing this command
     */
    public ServerCommand(Application<T> application, GizmoBundle bundle) {
        super(application, "gizmo", "gizmo web server.");
        configurationClass = application.getConfigurationClass();
        this.bundle = bundle;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T configuration) throws Exception {

        GizmoServer server = bundle.getServerFactory().build(environment);

        environment.lifecycle().attach(server);

        try {
            server.addLifeCycleListener(new LifeCycleListener());
            cleanupAsynchronously();
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.stop();
                } catch (Exception e) {
                    LOGGER.warn("Failure during stop server", e);
                }
            }));
        } catch (Exception e) {
            LOGGER.error("Unable to start server, shutting down", e);
            try {
                server.stop();
            } catch (Exception e1) {
                LOGGER.warn("Failure during stop server", e1);
            }
            try {
                cleanup();
            } catch (Exception e2) {
                LOGGER.warn("Failure during cleanup", e2);
            }
            throw e;
        }

    }

    private class LifeCycleListener extends AbstractLifeCycle.AbstractLifeCycleListener {
        @Override
        public void lifeCycleStopped(LifeCycle event) {
            cleanup();
        }

        @Override
        public void lifeCycleStarted(LifeCycle event) {
            if (event instanceof GizmoServer) {
                GizmoServer server = (GizmoServer) event;
                server.listenerInfos().forEach(it -> LOGGER.info(it.toString()));
            }
        }
    }
}
