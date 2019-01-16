/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.resteasy.setup;

import io.monkey.MonkeyException;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentManager;

import javax.inject.Provider;
import javax.servlet.ServletException;

/**
 * @author Michael
 * Created at: 2019/1/15 17:26
 */
class ResteasyHandlerProvider implements Provider<HttpHandler> {

    private DeploymentManager manager;

    ResteasyHandlerProvider(DeploymentManager manager) {
        this.manager = manager;
    }

    @Override
    public HttpHandler get() {
        try {
            manager.deploy();
            return manager.start();
        } catch (ServletException e) {
            throw new MonkeyException(e);
        }
    }
}
