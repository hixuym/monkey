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

package io.sunflower.resteasy.netty;

import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResteasyNettyServer
 *
 * @author michael
 * created on 17/11/6 15:53
 */
public class ResteasyNettyServer extends Server {

    private static Logger logger = LoggerFactory.getLogger(ResteasyNettyServer.class);

    private final NettyJaxrsServer server;

    public ResteasyNettyServer(NettyJaxrsServer server, Environment environment) {
        super(environment);
        this.server = server;
    }

    @Override
    protected void boot() throws Exception {
        server.start();
    }

    @Override
    protected void shutdown() throws Exception {
        server.stop();
    }

    public ResteasyDeployment getDeployment() {
        return server.getDeployment();
    }
}
