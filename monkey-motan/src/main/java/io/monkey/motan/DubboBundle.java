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

import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;

/**
 * @author Michael
 * Created at: 2019/1/3 22:32
 */
public abstract class DubboBundle<T extends Configuration> implements ConfiguredBundle<T>, DubboConfiguration<T> {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(T configuration, Environment environment) {

        DubboFactory dubboFactory = getDubboFactory(configuration);

        RegistryConfig registryConfig = dubboFactory.getRegistryConfig();

        ProtocolConfig protocolConfig = dubboFactory.getProtocolConfig();



        // export motan services
        environment.addServerLifecycleListener((server -> {}));

    }

}
