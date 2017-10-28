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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.*;
import io.undertow.server.HttpHandler;

import javax.inject.Provider;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * UndertowModule
 *
 * @author michael created on 17/10/17 11:16
 */
public class UndertowModule extends AbstractModule {

    private final Map<String, Class<? extends Provider<HttpHandler>>> adminHandlers = Maps
            .newHashMap();
    private final Map<String, Class<? extends Provider<HttpHandler>>> appHandlers = Maps.newHashMap();
    private final Set<Class<? extends Task>> taskSet = Sets.newHashSet();
    private final AdminTaskManager taskManager;

    public UndertowModule(Environment environment) {
        this.taskManager = new AdminTaskManager(environment);
        registryTask(LogConfigurationTask.class);
        registryTask(GarbageCollectionTask.class);
        registryTask(ThreadDumpTask.class);
    }

    @Override
    protected void configure() {
        // admin handlers;
        MapBinder<String, HttpHandler> adminMapBinder = MapBinder
                .newMapBinder(binder(), String.class, HttpHandler.class, AdminHandlers.class);

        for (Map.Entry<String, Class<? extends Provider<HttpHandler>>> e : adminHandlers.entrySet()) {
            requestInjection(e.getValue());
            adminMapBinder.addBinding(e.getKey()).toProvider(e.getValue());
        }

        bind(AdminTaskManager.class).toInstance(taskManager);

        adminMapBinder.addBinding("tasks").toInstance(taskManager);
        adminMapBinder.addBinding("healthcheck").to(HealthChecksHandler.class);
        adminMapBinder.addBinding("metrics").to(MetricsDisplayHandler.class);

        MapBinder<String, HttpHandler> appMapBinder =
                MapBinder.newMapBinder(binder(), String.class, HttpHandler.class, AppHandlers.class);

        for (Map.Entry<String, Class<? extends Provider<HttpHandler>>> e : appHandlers.entrySet()) {
            requestInjection(e.getValue());
            appMapBinder.addBinding(e.getKey()).toProvider(e.getValue());
        }

        taskSet.forEach(this::bind);
    }

    public void registryTask(Class<? extends Task> task) {
        this.taskSet.add(task);
    }

    public void registryAdminHandler(String path,
                                     Class<? extends Provider<HttpHandler>> httpHandler) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(httpHandler);
        this.adminHandlers.put(path, httpHandler);
    }

    public void registryApplicationHandler(String path,
                                           Class<? extends Provider<HttpHandler>> httpHandler) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(httpHandler);
        this.appHandlers.put(path, httpHandler);
    }
}
