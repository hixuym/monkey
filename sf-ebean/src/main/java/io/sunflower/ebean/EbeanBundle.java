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

package io.sunflower.ebean;

import javax.inject.Singleton;

import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

public abstract class EbeanBundle<T extends Configuration> implements ConfiguredBundle<T>, EbeanConfigurable<T> {

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        ServerConfigFactory serverConfigFactory = getServerConfigFactory(configuration);

        ServerConfig serverConfig = serverConfigFactory.build(environment);

        environment.guicey().addModule((binder) ->
            binder.bind(EbeanServer.class).toProvider(EbeanServerProvider.class).in(Singleton.class));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
