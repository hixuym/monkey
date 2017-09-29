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

package io.sunflower.testing;

import com.google.inject.Injector;

import net.sourceforge.argparse4j.inf.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.cli.EnvironmentCommand;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.setup.Environment;

public class TestCommand<T extends Configuration> extends EnvironmentCommand {

    private static Logger LOGGER = LoggerFactory.getLogger(TestCommand.class);

    private final Class<T> configurationClass;

    public TestCommand(Application<T> application) {
        super(application, "test", "use for test dao or services.");
        this.configurationClass = application.getConfigurationClass();
    }

    @Override
    public Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Environment environment, Namespace namespace, Configuration configuration) throws Exception {
        MockServer server = new MockServer();

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
            if (event instanceof MockServer) {
            }
        }
    }

}
