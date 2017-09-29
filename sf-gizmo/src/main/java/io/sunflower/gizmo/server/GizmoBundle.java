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

package io.sunflower.gizmo.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import javax.inject.Singleton;

import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.RouteBuilder;
import io.sunflower.gizmo.RouteBuilderImpl;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.RouterImpl;
import io.sunflower.gizmo.bodyparser.BodyParserEngineJson;
import io.sunflower.gizmo.bodyparser.BodyParserEnginePost;
import io.sunflower.gizmo.bodyparser.BodyParserEngineXml;
import io.sunflower.gizmo.params.ParamParser;
import io.sunflower.gizmo.template.TemplateEngineJson;
import io.sunflower.gizmo.template.TemplateEngineJsonP;
import io.sunflower.gizmo.template.TemplateEngineText;
import io.sunflower.gizmo.template.TemplateEngineXml;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.HandlerModule;

public class GizmoBundle<T extends Configuration> implements ConfiguredBundle<T> {

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        environment.guicey().addModule(new AbstractModule() {
            @Override
            protected void configure() {
                if (configuration.getServerFactory() instanceof GizmoConfiguration) {
                    bind(GizmoConfiguration.class).toInstance((GizmoConfiguration) configuration.getServerFactory());
                }

                // Routing
                Multibinder.newSetBinder(binder(), ParamParser.class);
                bind(RouteBuilder.class).to(RouteBuilderImpl.class);
                bind(Router.class).to(RouterImpl.class).in(Singleton.class);

                bind(BodyParserEnginePost.class);
                bind(BodyParserEngineXml.class);
                bind(BodyParserEngineJson.class);

                bind(TemplateEngineJson.class);
                bind(TemplateEngineJsonP.class);
                bind(TemplateEngineXml.class);
                bind(TemplateEngineText.class);

                bind(Context.class).to(UndertowContext.class);

                //
                install(new HandlerModule(environment));
            }
        });
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

}
