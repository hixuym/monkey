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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.jackson.Discoverable;
import io.sunflower.setup.Environment;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultGizmoServerFactory.class)
public interface GizmoServerFactory extends Discoverable {

    GizmoServer build(Environment environment);

    GizmoConfiguration gizmoConfig();
}
