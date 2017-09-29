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

package io.sunflower.testing;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;

import io.sunflower.Configuration;
import io.sunflower.configuration.ConfigurationSourceProvider;
import io.sunflower.configuration.YamlConfigurationFactory;

public class POJOConfigurationFactory<C extends Configuration>
    extends YamlConfigurationFactory<C> {
    protected final C configuration;

    @SuppressWarnings("unchecked")
    public POJOConfigurationFactory(C cfg) {
        super((Class<C>) cfg.getClass(), null, null, null);
        configuration = cfg;
    }

    @Override
    public C build(ConfigurationSourceProvider provider, String path) {
        return configuration;
    }

    @Override
    public C build(File file) {
        return configuration;
    }

    @Override
    public C build() {
        return configuration;
    }

    @Override
    protected C build(JsonNode node, String path) {
        return configuration;
    }
}
