/**
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.server;

import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

import io.sunflower.gizmo.Gizmo;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;
import io.sunflower.lifecycle.ContainerLifeCycle;
import io.sunflower.setup.Environment;
import io.undertow.Undertow;

/**
 * sunflower standalone based on Undertow.
 */
public class GizmoServer extends ContainerLifeCycle {

    private Logger logger = LoggerFactory.getLogger(GizmoServer.class);

    private final Environment environment;

    private Undertow undertow;
    private boolean undertowStarted;                      // undertow fails on stop() if start() never called

    protected SSLContext sslContext;

    private Gizmo gizmo;

    private final Injector injector;

    public GizmoServer(Environment environment, Undertow undertow) {
        this.undertow = undertow;
        this.environment = environment;
        this.injector = environment.guicey().injector();
        this.gizmo = injector.getInstance(Gizmo.class);
    }

    @Override
    public void doStart() throws Exception {

        super.doStart();

        this.initRoutes();

        this.gizmo.onFrameworkStart();

        String version = undertow.getClass().getPackage().getImplementationVersion();
        logger.info("Trying to start undertow v{}", version);
        this.undertow.start();
        undertowStarted = true;
        logger.info("Started undertow v{}", version);
    }

    private void initRoutes() {

        Router router = injector.getInstance(Router.class);

        injector.getInstance(ApplicationRoutes.class).init(router);

        router.compileRoutes();
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();

        this.gizmo.onFrameworkShutdown();

        if (this.undertow != null && undertowStarted) {
            logger.info("Trying to stop undertow.");
            this.undertow.stop();
            logger.info("Stopped undertow.");
            this.undertow = null;
        }
    }

}
