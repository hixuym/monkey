/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.gizmo.conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import org.slf4j.Logger;

import io.sunflower.gizmo.RouteBuilder;
import io.sunflower.gizmo.RouteBuilderImpl;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.RouterImpl;
import io.sunflower.gizmo.params.ParamParser;
import io.sunflower.gizmo.server.GizmoConfiguration;
import io.sunflower.gizmo.utils.LoggerProvider;

/**
 * The basic configuration of the main ninja framework.
 */
public class NinjaBaseModule extends AbstractModule {

    private final GizmoConfiguration ninjaProperties;

    public NinjaBaseModule(GizmoConfiguration ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public void configure() {
        System.setProperty("file.encoding", "utf-8");

        // Routing
        Multibinder.newSetBinder(binder(), ParamParser.class);
        bind(RouteBuilder.class).to(RouteBuilderImpl.class);
        bind(Router.class).to(RouterImpl.class).in(Singleton.class);

        // Logging
        bind(Logger.class).toProvider(LoggerProvider.class);

        // Bind the configuration into Guice
        bind(GizmoConfiguration.class).toInstance(ninjaProperties);
    }

}
