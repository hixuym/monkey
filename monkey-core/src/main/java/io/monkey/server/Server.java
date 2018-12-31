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

package io.monkey.server;

import com.google.common.base.Stopwatch;
import io.monkey.lifecycle.ContainerLifeCycle;
import io.monkey.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michael
 */
public abstract class Server extends ContainerLifeCycle {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public Server(Environment environment) {
        environment.lifecycle().attach(this);
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
    protected void boot() throws Exception {}

    /**
     * shutdown the real server
     *
     */
    protected void shutdown() throws Exception {}

}
