/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.cli;

import io.monkey.Application;
import io.monkey.Configuration;
import io.monkey.lifecycle.AbstractLifeCycle;
import io.monkey.lifecycle.LifeCycle;
import io.monkey.server.Server;
import io.monkey.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a application as an HTTP server.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 * @author michael
 */
public class ServerCommand<T extends Configuration> extends EnvironmentCommand<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerCommand.class);

    private final Class<T> configurationClass;

    public ServerCommand(Application<T> application) {
        this(application, "server", "Runs the monkey application as server");
    }

    /**
     * A constructor to allow reuse of the server command as a different name
     *
     * @param application the application using this command
     * @param name        the argument name to invoke this command
     * @param description a summary of what the command does
     */
    protected ServerCommand(final Application<T> application, final String name,
                            final String description) {
        super(application, name, description);
        this.configurationClass = application.getConfigurationClass();
    }

    /**
     * Since we don't subclass ServerCommand, we need a concrete reference to the configuration
     * class.
     */
    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T configuration)
            throws Exception {
        final Server server = configuration.getServerFactory().build(environment);

        try {
            server.addLifeCycleListener(new LifeCycleListener());
            cleanupAsynchronously();
            server.start();
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
    }
}
