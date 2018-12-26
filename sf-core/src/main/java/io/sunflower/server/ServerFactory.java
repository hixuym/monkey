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

package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sunflower.json.Discoverable;
import io.sunflower.setup.Environment;

/**
 * A factory for building {@link Server} instances for sunflower applications.
 *
 * @author michael
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = FakeServerFactory.class)
public interface ServerFactory extends Discoverable {

    /**
     * Build a server for the given Sunflower application.
     *
     * @param environment the application's environment
     * @return a {@link Server} running the Dropwizard application
     */
    Server build(Environment environment);

    /**
     * Configures the given environment with settings defined in the factory.
     *
     * @param environment the application's environment
     */
    void configure(Environment environment);

}
