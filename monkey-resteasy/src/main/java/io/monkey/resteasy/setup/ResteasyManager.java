/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.resteasy.setup;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import io.monkey.lifecycle.Managed;
import io.monkey.setup.Environment;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael
 * Created at: 2019/1/15 17:14
 */
class ResteasyManager implements Managed {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyManager.class);

    private final Environment environment;
    private final ResteasyDeployment deployment;
    private final String contextPath;

    ResteasyManager(Environment environment, ResteasyDeployment deployment, String contextPath) {
        this.environment = environment;
        this.deployment = deployment;
        this.contextPath = contextPath;
    }

    @Override
    public void start() {
        scanResources(environment.getInjector());
        logger.info("JAX-RS WADL at: {}", (contextPath.endsWith("/") ? contextPath : contextPath + "/") + "application.xml");
    }

    @Override
    public void stop() {
        deployment.stop();
    }

    public ResteasyDeployment getDeployment() {
        return deployment;
    }

    private void scanResources(Injector injector) {
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
                    logger.info("registering provider instance for {}", beanClass.getName());
                    deployment.getProviderFactory().registerProviderInstance(binding.getProvider().get());
                }

            }
        }

        for (Binding<?> binding : rootResourceBindings) {

            Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();

            if (Scopes.isSingleton(binding)) {
                logger.info("registering singleton factory for {}, make sure threadsafe.", beanClass.getName());
                deployment.getRegistry().addSingletonResource(binding.getProvider().get());
            } else {
                final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
                logger.info("registering factory for {}", beanClass.getName());
                deployment.getRegistry().addResourceFactory(resourceFactory);
            }

        }
    }
}
