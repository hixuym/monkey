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

package io.sunflower.undertow;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.AdminTaskManager;
import io.sunflower.undertow.handler.GarbageCollectionTask;
import io.sunflower.undertow.handler.HealthChecksHandler;
import io.sunflower.undertow.handler.LogConfigurationTask;
import io.sunflower.undertow.handler.MetricsDisplayHandler;
import io.sunflower.undertow.handler.ThreadDumpTask;
import io.undertow.server.HttpHandler;

/**
 * UndertowModule
 *
 * @author michael created on 17/10/17 11:16
 */
public class UndertowModule extends AbstractModule {

  private final Environment environment;

  public UndertowModule(Environment environment) {
    this.environment = environment;
  }

  @Override
  protected void configure() {
    // admin handlers;
    MapBinder<String, HttpHandler> adminMapBinder = MapBinder
        .newMapBinder(binder(), String.class, HttpHandler.class, AdminHandlers.class);

    AdminTaskManager taskManager = new AdminTaskManager(environment);

    bind(AdminTaskManager.class).toInstance(taskManager);
    adminMapBinder.addBinding("tasks").toInstance(taskManager);
    adminMapBinder.addBinding("healthcheck").to(HealthChecksHandler.class);
    adminMapBinder.addBinding("metrics").to(MetricsDisplayHandler.class);

    MapBinder.newMapBinder(binder(), String.class, HttpHandler.class, AppHandlers.class);

    bind(LogConfigurationTask.class);
    bind(GarbageCollectionTask.class);
    bind(ThreadDumpTask.class);
  }
}
