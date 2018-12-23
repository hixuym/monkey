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

package io.sunflower.jaxrs.server;

import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JaxrsServer
 *
 * @author michael
 * created on 17/11/6 15:53
 */
class JaxrsServer extends Server {

    private static Logger logger = LoggerFactory.getLogger(JaxrsServer.class);

    private final NettyJaxrsServer server;
    private final String resteasyVer;
    private boolean started;

    private static final String ROOT = "/";
    static final String HTTP = "http";
    static final String HTTPS = "https";

    private String applicationContextPath = ROOT;
    private String schema = HTTP;

    JaxrsServer(NettyJaxrsServer server, Environment environment) {
        super(environment);
        this.server = server;
        this.resteasyVer = ResteasyDeployment.class.getPackage().getImplementationVersion();
    }

    @Override
    protected void boot() {

        server.start();

        started = true;

        logger.info("Started JAX-RS Server({})", resteasyVer);

        logger.info("JAX-RS WADL at: {}", this.schema + "://"
                + (server.getHostname() == null ? "localhost" : server.getHostname())
                + ":" + server.getPort()
                + applicationContextPath
                + "/application.xml");
    }

    @Override
    protected void shutdown() {
        if (server != null && started) {
            server.stop();
            logger.info("Stopped JAX-RS Server({})", resteasyVer);
        }
    }

    void setSchema(String schema) {
        this.schema = schema;
    }

    void setApplicationContextPath(String applicationContextPath) {
        if (applicationContextPath != null && ROOT.equals(applicationContextPath)) {
            this.applicationContextPath = "";
        } else {
            this.applicationContextPath = applicationContextPath;
        }
    }
}
