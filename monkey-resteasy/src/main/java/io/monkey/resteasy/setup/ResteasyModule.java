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

package io.monkey.resteasy.setup;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import io.monkey.resteasy.actuator.ApplicationActuatorResource;
import io.monkey.resteasy.caching.CacheControlledResponseFeature;
import io.monkey.resteasy.errors.ErrorsMapperFeature;
import io.monkey.resteasy.jackson.JacksonFeature;
import io.monkey.resteasy.optional.OptionalParamFeature;
import io.monkey.resteasy.params.BasicParamFeature;
import io.monkey.resteasy.validation.HibernateValidationFeature;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author michael
 */
class ResteasyModule extends AbstractModule {

    private final DeploymentManager manager;
    private final String contextPath;

    public ResteasyModule(DeploymentManager manager, String contextPath) {
        this.manager = manager;
        this.contextPath = contextPath;
    }

    @Override
    public void configure() {

        bind(RuntimeDelegate.class).toInstance(RuntimeDelegate.getInstance());
        bind(Response.ResponseBuilder.class).toProvider(ResponseBuilderProvider.class);
        bind(UriBuilder.class).toProvider(UriBuilderProvider.class);
        bind(Variant.VariantListBuilder.class).toProvider(VariantListBuilderProvider.class);

        bind(HibernateValidationFeature.class);
        bind(JacksonFeature.class);
        bind(BasicParamFeature.class);
        bind(OptionalParamFeature.class);
        bind(CacheControlledResponseFeature.class);
        bind(ErrorsMapperFeature.class);

        bind(ResteasyWadlDefaultResource.class);
        bind(ApplicationActuatorResource.class);

        MapBinder<String, HttpHandler> mapBinder = MapBinder.newMapBinder(binder(), String.class, HttpHandler.class);
        mapBinder.addBinding(contextPath).toProvider(new ResteasyHandlerProvider(manager));
    }

}
