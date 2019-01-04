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

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.core.extension.ExtensionLoader;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.motan.setup.MotanModule;
import io.monkey.server.Server;
import io.monkey.server.ServerLifecycleListener;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import io.monkey.setup.GuicifyEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        environment.guicify().register(new MotanModule(motanFactory, environment));

        // export motan services
        environment.addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server) {
                MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
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

}
