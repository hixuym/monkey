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

package io.monkey.motan.setup;

import com.weibo.api.motan.core.extension.ExtensionLoader;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.motan.MotanConfiguration;
import io.monkey.motan.MotanFactory;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael
 * Created at: 2019/1/3 22:32
 */
public class MotanBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static Logger logger = LoggerFactory.getLogger(MotanBundle.class);

    private final MotanConfiguration<T> motanConfiguration;

    public MotanBundle(MotanConfiguration<T> motanConfiguration) {
        this.motanConfiguration = motanConfiguration;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T configuration, Environment environment) {

        final MotanFactory motanFactory = motanConfiguration.getMotanFactory(configuration);

        environment.guicify().register(new MotanModule(motanFactory, environment));

        // export motan services
        environment.lifecycle().manage(new MotanManager(motanFactory));
    }

    public <E> MotanBundle addSpi(Class<E> interfaceClass, Class<E> implClass) {
        ExtensionLoader.getExtensionLoader(interfaceClass).addExtensionClass(implClass);
        return this;
    }

}
