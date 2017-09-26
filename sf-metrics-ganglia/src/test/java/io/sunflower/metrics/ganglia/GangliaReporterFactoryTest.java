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

package io.sunflower.metrics.ganglia;

import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.DiscoverableSubtypeResolver;
import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GangliaReporterFactoryTest {

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
            .contains(GangliaReporterFactory.class);
    }

    @Test
    public void createDefaultFactory() throws Exception {
        final GangliaReporterFactory factory = new YamlConfigurationFactory<>(GangliaReporterFactory.class,
            BaseValidator.newValidator(), Jackson.newObjectMapper(), "sf")
            .build();
        assertThat(factory.getFrequency()).isEqualTo(Optional.empty());
    }
}
