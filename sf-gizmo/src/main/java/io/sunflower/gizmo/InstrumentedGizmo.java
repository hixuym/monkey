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

package io.sunflower.gizmo;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.sunflower.gizmo.exceptions.BadRequestException;

/**
 * Created by michael on 17/6/23.
 */
@Singleton
public class InstrumentedGizmo extends GizmoDefault {

    private static final String METER_ALL_REQUESTS = "gizmo.requests.allRequests";
    private static final String COUNTER_ACTIVE_REQUESTS = "gizmo.requests.activeRequests";
    private static final String METER_BAD_REQUESTS = "gizmo.requests.badRequests";
    private static final String METER_INTERNAL_SERVER_ERRORS = "gizmo.requests.internalServerErrors";
    private static final String METER_ROUTES_NOT_FOUND = "gizmo.requests.routesNotFound";

    private Meter allRequestsMeter;
    private Counter activeRequests;
    private Meter badRequests;
    private Meter internalServerErrors;
    private Meter routesNotFound;

    private final MetricRegistry metricRegistry;

    @Inject
    public InstrumentedGizmo(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        allRequestsMeter = metricRegistry.meter(METER_ALL_REQUESTS);
        activeRequests = metricRegistry.counter(COUNTER_ACTIVE_REQUESTS);
        badRequests = metricRegistry.meter(METER_BAD_REQUESTS);
        internalServerErrors = metricRegistry.meter(METER_INTERNAL_SERVER_ERRORS);
        routesNotFound = metricRegistry.meter(METER_ROUTES_NOT_FOUND);
    }

    @Override
    public void onRouteRequest(Context.Impl context) {

        Timer timer = metricRegistry.timer("gizmo.routeRequest");

        Timer.Context timerContext = timer.time();

        try {
            activeRequests.inc();

            String httpMethod = context.getMethod();

            Route route = router.getRouteFor(httpMethod, context.getRequestPath());

            if (route == null) {
                routesNotFound.mark();
                Result result = exceptionHandler.getNotFoundResult(context);
                renderErrorResultAndCatchAndLogExceptions(result, context);
                return;
            }

            allRequestsMeter.mark();
            context.setRoute(route);

            Result underlyingResult = null;
            try {
                underlyingResult = route.getFilterChain().next(context);
                resultHandler.handleResult(underlyingResult, context);
            } catch (Exception exception) {
                if (exception instanceof BadRequestException) {
                    badRequests.mark();
                } else {
                    internalServerErrors.mark();
                }
                Result result = exceptionHandler.onException(context, exception, underlyingResult);
                renderErrorResultAndCatchAndLogExceptions(result, context);
            } finally {
                context.cleanup();
                activeRequests.dec();
            }
        } finally {
            timerContext.stop();
        }
    }
}
