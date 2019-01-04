/*
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

package io.monkey.motan;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.*;
import com.weibo.api.motan.core.extension.ExtensionLoader;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.motan.annotation.MotanService;
import io.monkey.server.Server;
import io.monkey.server.ServerLifecycleListener;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import io.monkey.setup.GuicifyEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.lang.reflect.Type;

/**
 * @author Michael
 * Created at: 2019/1/3 22:32
 */
public abstract class MotanBundle<T extends Configuration> implements ConfiguredBundle<T>, MotanConfiguration<T> {

    private static Logger logger = LoggerFactory.getLogger(MotanBundle.class);

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T configuration, Environment environment) {

        final MotanFactory motanFactory = getMotanFactory(configuration);

        bindService(environment.guicify());

        environment.guicify().register(new MotanModule(motanFactory));

        // export motan services
        environment.addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server) {
                scanAndExportMotanService(environment.getInjector(), motanFactory);
            }

            @Override
            public void serverStopping(Server server) {
                motanFactory.getServicesConfig().values().forEach(ServiceConfig::unexport);
                motanFactory.getReferersConfig().values().forEach(RefererConfig::destroy);
                MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
            }
        });
    }

    protected void bindService(GuicifyEnvironment environment) {
    }

    protected <E> void addSpi(Class<E> interfaceClass, Class<E> implClass) {
        ExtensionLoader.getExtensionLoader(interfaceClass).addExtensionClass(implClass);
    }

    private void scanAndExportMotanService(Injector injector, MotanFactory motanFactory) {
        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getRawType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;

                if (beanClass.isAnnotationPresent(MotanService.class)) {
                    logger.info("registering motan service instance for {}", beanClass.getName());
                    registerServiceConfig(beanClass, binding.getProvider(), motanFactory);
                }
            }
        }

        motanFactory.getServicesConfig().values().forEach(ServiceConfig::export);

        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
    }

    private void registerServiceConfig(Class<?> beanClass, Provider provider, MotanFactory motanFactory) {
        MotanService service = beanClass.getAnnotation(MotanService.class);

        String id = service.id();

        if (Strings.isNullOrEmpty(id)) {
            id = beanClass.getName();
        }

        ServiceConfig serviceConfig = motanFactory.getServicesConfig().get(id);

        if (serviceConfig == null) {
            serviceConfig = new ServiceConfig();
        }

        serviceConfig.setRef(provider.get());
        serviceConfig.setInterface(beanClass);

        if (Strings.isNullOrEmpty(serviceConfig.getExport()) && !Strings.isNullOrEmpty(service.export())) {
            serviceConfig.setExport(service.export());
        }

        motanFactory.registerService(id, serviceConfig);

    }

}
