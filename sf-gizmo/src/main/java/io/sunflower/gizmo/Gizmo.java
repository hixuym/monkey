/**
 * Copyright (C) 2012-2017 the original author or authors.
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

package io.sunflower.gizmo;

public interface Gizmo {

    /**
     * When a route is requested this method is called.
     */
    void onRouteRequest(Context.Impl context);

    /**
     * Invoked when the framework starts. Usually inits stuff like the scheduler and so on.
     */
    void onFrameworkStart();

    /**
     * Invoked when the server hosting Gizmo is being stopped. Usually shuts down the guice injector and stopps all
     * services.
     */
    void onFrameworkShutdown();

    /**
     * Should be used to render an error. Any errors should be catched and not reported in any way to the request.
     *
     * For instance if your application catches a sever internal computation error use this method and its
     * implementations to render out an error html page.
     */
    void renderErrorResultAndCatchAndLogExceptions(Result errorResult, Context context);

}