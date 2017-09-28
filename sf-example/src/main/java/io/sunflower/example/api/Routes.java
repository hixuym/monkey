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

package io.sunflower.example.api;

import io.sunflower.example.resources.ApplicationResource;
import io.sunflower.gizmo.AssetsResource;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {

        router.GET().route("/").with(ApplicationResource::index);
        router.GET().route("/hello_world.json").with(ApplicationResource::helloWorldJson);
        router.GET().route("/user_count.json").with(ApplicationResource::userCount);

        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsResource::serveWebJars);
        router.GET().route("/assets/{fileName: .*}").with(AssetsResource::serveStatic);

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(ApplicationResource::index);
    }
}
