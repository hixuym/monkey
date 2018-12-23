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

package io.sunflower.server;

import com.google.common.base.Stopwatch;
import com.google.inject.Injector;
import io.sunflower.inject.lifecycle.LifecycleManager;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.setup.Environment;
import io.sunflower.setup.GuiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michael
 */
public abstract class Server extends ContainerLifeCycle {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;
    private Injector injector;
    private GuiceConfig guiceConfig;

    public Server(Environment environment) {
        this.environment = environment;
        environment.lifecycle().attach(this);
        this.injector = environment.injector();
        this.guiceConfig = environment.guice().getGuiceConfig();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.stop();
            } catch (Exception e) {
                logger.warn("Failure during stop server", e);
            }
        }));
    }

    @Override
    protected final void doStart() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        if (guiceConfig.isLifecycleEnabled()) {
            injector.getInstance(LifecycleManager.class).start();
        }
        super.doStart();
        this.boot();
        sw.stop();
        if (logger.isInfoEnabled()) {
            logger.info("Server started in {}", sw);
        }
    }

    @Override
    protected final void doStop() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        if (guiceConfig.isLifecycleEnabled()) {
            injector.getInstance(LifecycleManager.class).stop();
        }
        super.doStop();
        this.shutdown();
        sw.stop();
        if (logger.isInfoEnabled()) {
            logger.info("Server stoped in {}", sw);
        }
    }

    /**
     * boot the real server
     *
     */
    protected void boot() {}

    /**
     * shutdown the real server
     *
     */
    protected void shutdown() {}

    public Environment getEnvironment() {
        return environment;
    }

    public Injector getInjector() {
        return injector;
    }
}
