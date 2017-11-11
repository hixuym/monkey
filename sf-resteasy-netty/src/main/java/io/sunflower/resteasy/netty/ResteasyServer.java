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

import io.netty.util.NettyRuntime;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResteasyServer
 *
 * @author michael
 * created on 17/11/6 15:53
 */
public class ResteasyServer extends Server {

    private static Logger logger = LoggerFactory.getLogger(ResteasyServer.class);

    private final NettyJaxrsServer server;
    private final String nettyVersion;
    private boolean nettyStarted;

    private static final String ROOT = "/";

    private String applicationContextPath = ROOT;

    private String schema = "http";

    public ResteasyServer(NettyJaxrsServer server, Environment environment) {
        super(environment);
        this.server = server;
        this.nettyVersion = NettyRuntime.class.getPackage().getImplementationVersion();
    }

    @Override
    protected void boot() throws Exception {
        logger.info("Trying to start netty v{}", nettyVersion);

        server.start();

        nettyStarted = true;

        logger.info("Started netty v{}", nettyVersion);

        logger.info("Resteasy WADL at: {}", this.schema + "://"
                + (server.getHostname() == null ? "localhost" : server.getHostname())
                + ":" + server.getPort()
                + applicationContextPath
                + "/application.xml");
    }

    @Override
    protected void shutdown() throws Exception {
        if (server != null && nettyStarted) {
            logger.info("Trying to stop netty {}", nettyVersion);
            server.stop();
            logger.info("Stopped netty v{}", nettyVersion);
        }
    }

    public ResteasyDeployment getDeployment() {
        return server.getDeployment();
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setApplicationContextPath(String applicationContextPath) {
        if (applicationContextPath != null && ROOT.equals(applicationContextPath)) {
            this.applicationContextPath = "";
        } else {
            this.applicationContextPath = applicationContextPath;
        }
    }
}
