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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.ssl.SslContextFactoryFactory;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;

import javax.net.ssl.SSLContext;

/**
 * HttpsServerFactory
 *
 * @author michael
 * created on 17/11/10 21:15
 */
@JsonTypeName("https")
public class HttpsServerFactory extends JaxrsServerFactory {


    @JsonProperty("sslcontext")
    private SslContextFactoryFactory sslContextFactory = new SslContextFactoryFactory();

    public SslContextFactoryFactory getSslContextFactory() {
        return sslContextFactory;
    }

    public void setSslContextFactory(SslContextFactoryFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
    }

    @Override
    protected Server buildServer(NettyJaxrsServer server, Environment environment) {

        final SSLContext sslContext = sslContextFactory.build(environment);

        server.setSSLContext(sslContext);

        JaxrsServer jaxrsServer = new JaxrsServer(server, environment);

        jaxrsServer.setApplicationContextPath(getApplicationContextPath());
        jaxrsServer.setSchema(JaxrsServer.HTTPS);

        return jaxrsServer;
    }

}
