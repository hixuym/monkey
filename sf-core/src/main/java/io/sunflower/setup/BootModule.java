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

package io.sunflower.setup;

import javax.validation.Validator;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * BootModule
 *
 * @author michael
 * @date 17/10/12 11:13
 */
public class BootModule extends AbstractModule {

  private final Environment environment;

  public BootModule(Environment environment) {
    this.environment = environment;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("application.name")).to(environment.getName());
    bind(ObjectMapper.class).toInstance(environment.getObjectMapper());
    bind(MetricRegistry.class).toInstance(environment.metrics());
    bind(HealthCheckRegistry.class).toInstance(environment.healthChecks());
    bind(Validator.class).toInstance(environment.getValidator());
    bind(Environment.class).toInstance(environment);
  }
}
