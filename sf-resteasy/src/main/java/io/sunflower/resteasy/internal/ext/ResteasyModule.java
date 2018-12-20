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

package io.sunflower.resteasy.internal.ext;

import com.google.inject.AbstractModule;
import io.sunflower.resteasy.jackson.JacksonFeature;
import io.sunflower.resteasy.validation.HibernateValidationFeature;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author michael
 */
public class ResteasyModule extends AbstractModule {

    @Override
    public void configure() {

        bind(ClientHttpEngine.class).to(ApacheHttpClient4Engine.class);

        bind(RuntimeDelegate.class).toInstance(RuntimeDelegate.getInstance());
        bind(Response.ResponseBuilder.class).toProvider(ResponseBuilderProvider.class);
        bind(UriBuilder.class).toProvider(UriBuilderProvider.class);
        bind(Variant.VariantListBuilder.class).toProvider(VariantListBuilderProvider.class);

        bind(HibernateValidationFeature.class);
        bind(JacksonFeature.class);
    }

}
