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

package com.example.conf;

import com.example.resources.DummyApplication;

import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;


// Just a dummy for testing.
// Allows to check that custom Ninja configuration in user's conf directory
// works properly.
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {

        router.GET().route("/").with(DummyApplication.class, "dummyMethod");

    }

}
