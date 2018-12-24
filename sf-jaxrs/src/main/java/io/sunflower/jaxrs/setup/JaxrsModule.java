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

package io.sunflower.jaxrs.setup;

import com.google.inject.AbstractModule;
import io.sunflower.jaxrs.actuator.ApplicationActuatorResource;
import io.sunflower.jaxrs.caching.CacheControlledResponseFeature;
import io.sunflower.jaxrs.errors.ErrorsMapperFeature;
import io.sunflower.jaxrs.jackson.JacksonFeature;
import io.sunflower.jaxrs.optional.OptionalParamFeature;
import io.sunflower.jaxrs.params.BasicParamFeature;
import io.sunflower.jaxrs.validation.HibernateValidationFeature;
import org.jboss.resteasy.wadl.ResteasyWadlDefaultResource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author michael
 */
public class JaxrsModule extends AbstractModule {

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
    }

}
