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

package io.sunflower.resteasy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.resteasy.internal.ResteasyResourcesRegister;
import io.sunflower.resteasy.internal.ext.ResteasyModule;
import io.sunflower.resteasy.internal.ext.RequestScopeModule;
import io.sunflower.resteasy.jackson.JacksonFeature;
import io.sunflower.resteasy.validation.HibernateValidationFeature;
import io.sunflower.resteasy.validation.Validators;
import io.sunflower.server.AbstractServerFactory;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * ResteasyNettyServerFactory
 *
 * @author michael
 * created on 17/11/6 15:52
 */
@JsonTypeName("resteasy")
public class ResteasyNettyServerFactory extends AbstractServerFactory {

    private ResteasyDeployment deployment = new ResteasyDeployment();

    @JsonProperty
    private int port = 8080;

    @JsonProperty
    private String contextPath = "";

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public Server build(Environment environment) {

        NettyJaxrsServer jaxrsServer = new NettyJaxrsServer();

        jaxrsServer.setDeployment(deployment);
        jaxrsServer.setPort(port);
        jaxrsServer.setRootResourcePath(contextPath);
        jaxrsServer.setSecurityDomain(null);

        environment.addServerLifecycleListener(server -> {
            if (server instanceof ResteasyNettyServer) {
                ResteasyDeployment deployment = ((ResteasyNettyServer) server).getDeployment();

                Registry registry = deployment.getRegistry();

                ResteasyProviderFactory providerFactory = deployment.getProviderFactory();

                new ResteasyResourcesRegister(registry, providerFactory).withInjector(server.getInjector());
            }
        });

        return new ResteasyNettyServer(jaxrsServer, environment);
    }

    @Override
    public void configure(Environment environment) {
        environment.setValidatorFactory(Validators.newValidatorFactory());
        environment.guice().register(new ResteasyModule(), new RequestScopeModule());
        environment.guice().register(JacksonFeature.class);
        environment.guice().register(HibernateValidationFeature.class);
    }

}
