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
package conf;

import com.google.inject.AbstractModule;

import io.sunflower.gizmo.server.GizmoConfiguration;

// Just a dummy for testing.
// Allows to check that custom Ninja module in user's conf directory
// works properly.
public class Module extends AbstractModule {

    GizmoConfiguration configuration;

    public Module(GizmoConfiguration configuration) {

        if (configuration == null) {
            throw new IllegalArgumentException("Received null as an instance of NinjaProperties");
        }

        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        bind(DummyInterfaceForTesting.class).to(DummyClassForTesting.class);

    }

    public static interface DummyInterfaceForTesting {
    }

    public static class DummyClassForTesting implements DummyInterfaceForTesting {
    }

}
