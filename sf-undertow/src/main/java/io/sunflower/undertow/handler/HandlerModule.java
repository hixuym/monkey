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

package io.sunflower.undertow.handler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

import io.undertow.server.HttpHandler;

public class HandlerModule extends AbstractModule {
    @Override
    protected void configure() {
        // admin handlers;
        MapBinder<String, HttpHandler> mapBinder = MapBinder.newMapBinder(binder(), String.class, HttpHandler.class);

        mapBinder.addBinding("tasks").to(TaskHandler.class);

        // tasks
        bind(LogConfigurationTask.class);
        bind(GarbageCollectionTask.class);
        bind(ThreadDumpTask.class);

        mapBinder.addBinding("healthcheck").to(HealthChecksHandler.class);
        mapBinder.addBinding("metrics").to(MetricsHandler.class);

//        mapBinder.addBinding("cpuprofiler").toInstance(Handlers.blocking(new CpuProfileHandler()));
    }
}
