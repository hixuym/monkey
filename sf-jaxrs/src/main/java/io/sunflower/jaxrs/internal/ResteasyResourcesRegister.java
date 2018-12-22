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

package io.sunflower.jaxrs.internal;

import com.google.inject.Binding;
import com.google.inject.Injector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author michael
 */
public class ResteasyResourcesRegister {

    private static Logger LOGGER = LoggerFactory.getLogger(ResteasyResourcesRegister.class);

    private final Registry registry;
    private final ResteasyProviderFactory providerFactory;

    public ResteasyResourcesRegister(final Registry registry, final ResteasyProviderFactory providerFactory) {
        this.registry = registry;
        this.providerFactory = providerFactory;
    }

    public void withInjector(final Injector injector) {
        List<Binding<?>> rootResourceBindings = new ArrayList<>();
        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getRawType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;
                if (GetRestful.isRootResource(beanClass)) {
                    // deferred registration
                    rootResourceBindings.add(binding);
                }

                if (beanClass.isAnnotationPresent(Provider.class)) {
                    LOGGER.info("registering provider instance for {}", beanClass.getName());
                    providerFactory.registerProviderInstance(binding.getProvider().get());
                }
            }
        }

        for (Binding<?> binding : rootResourceBindings) {

            Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();
            final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
            LOGGER.info("registering factory for {}", beanClass.getName());
            registry.addResourceFactory(resourceFactory);

        }
    }
}
