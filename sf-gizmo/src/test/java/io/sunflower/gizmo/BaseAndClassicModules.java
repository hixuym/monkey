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

package io.sunflower.gizmo;

import com.google.inject.AbstractModule;

import io.sunflower.gizmo.conf.NinjaBaseModule;
import io.sunflower.gizmo.conf.NinjaClassicModule;
import io.sunflower.gizmo.server.GizmoConfiguration;

/**
 * The basic configuration of the main ninja framework.
 */
public class BaseAndClassicModules extends AbstractModule {

    private final GizmoConfiguration configuration;

    public BaseAndClassicModules(GizmoConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void configure() {
        install(new NinjaBaseModule(configuration));
        install(new NinjaClassicModule(configuration));
    }

}