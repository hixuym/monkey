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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sunflower.resteasy.internal.ResteasyResourcesRegister;
import io.sunflower.resteasy.internal.ext.RequestScopeModule;
import io.sunflower.resteasy.internal.ext.ResteasyModule;
import io.sunflower.server.AbstractServerFactory;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;
import org.jboss.resteasy.wadl.ResteasyWadlGenerator;

/**
 * AbstractResteasyServerFactory
 *
 * @author michael
 * created on 17/11/10 15:53
 */
public abstract class AbstractResteasyServerFactory extends AbstractServerFactory {

    private final ResteasyDeployment deployment = new ResteasyDeployment();

    private String applicationContextPath = "";

    private int port = 8080;

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public final Server build(Environment environment) {

        environment.addServerLifecycleListener(server -> {
            Registry registry = deployment.getRegistry();
            ResteasyProviderFactory providerFactory = deployment.getProviderFactory();
            new ResteasyResourcesRegister(registry, providerFactory).withInjector(server.getInjector());
            ResteasyWadlDefaultResource.getServices().put("/", ResteasyWadlGenerator.generateServiceRegistry(deployment));
        });

        return buildServer(environment);
    }

    /**
     * build the real server
     *
     * @param environment
     * @return
     */
    protected abstract Server buildServer(Environment environment);

    protected void configure(ResteasyDeployment deployment) {
    }

    @Override
    public void configure(Environment environment) {
        environment.guice().register(new ResteasyModule(), new RequestScopeModule());
        environment.guice().register(ResteasyWadlDefaultResource.class);
    }

    @JsonIgnore
    public ResteasyDeployment getDeployment() {
        return deployment;
    }
}
