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

package io.monkey.resteasy.actuator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.HealthCheckModule;
import com.codahale.metrics.json.MetricsModule;
import com.codahale.metrics.jvm.ThreadDump;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.monkey.resteasy.params.IntParam;
import io.monkey.setup.Environment;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationActuatorResource
 *
 * @author michael
 * created on 17/11/10 22:30
 */
@Singleton
@Path("/__admin")
public class ApplicationActuatorResource {

    private final Environment environment;

    @Inject
    public ApplicationActuatorResource(Environment environment) {
        this.environment = environment;
    }

    @Path("/metrics")
    @GET
    public Response metrics() {

        MetricRegistry metricRegistry = environment.metrics();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.SECONDS, true));

        StreamingOutput output = out -> mapper.writerWithDefaultPrettyPrinter().writeValue(out, metricRegistry);

        return Response.ok()
                .cacheControl(CacheControl.valueOf("must-revalidate,no-cache,no-store"))
                .type(MediaType.APPLICATION_JSON)
                .entity(output)
                .build();
    }

    @Path("/ping")
    @GET
    public Response ping() {
        return Response.status(Response.Status.OK)
                .cacheControl(CacheControl.valueOf("must-revalidate,no-cache,no-store"))
                .type(MediaType.TEXT_PLAIN)
                .entity("pong")
                .build();
    }

    @Path("/healthcheck")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {

        HealthCheckRegistry healthCheckRegistry = environment.healthChecks();

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new HealthCheckModule());

        SortedMap<String, HealthCheck.Result> results = healthCheckRegistry.runHealthChecks(environment.getHealthCheckExecutorService());

        StreamingOutput output = out -> mapper.writerWithDefaultPrettyPrinter().writeValue(out, results);

        Response.ResponseBuilder responseBuilder = Response.ok()
                .cacheControl(CacheControl.valueOf("must-revalidate,no-cache,no-store"))
                .entity(output);

        if (!isAllHealthy(results)) {
            responseBuilder.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return responseBuilder.build();
    }

    private static boolean isAllHealthy(Map<String, HealthCheck.Result> results) {
        for (HealthCheck.Result result : results.values()) {
            if (!result.isHealthy()) {
                return false;
            }
        }
        return true;
    }

    @Path("/gc")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String gc(@QueryParam("n") @DefaultValue("3") IntParam gcCount) {

        int n = gcCount.get();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {

            sb.append("Running GC...\n");
            Runtime.getRuntime().gc();

        }

        sb.append("done");

        return sb.toString();
    }

    @Path("/thread-dump")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response threadDump() {

        ThreadDump threadDump = new ThreadDump(ManagementFactory.getThreadMXBean());

        StreamingOutput output = threadDump::dump;

        return Response.ok()
                .cacheControl(CacheControl.valueOf("must-revalidate,no-cache,no-store"))
                .type(MediaType.TEXT_PLAIN)
                .entity(output)
                .build();
    }

    @Path("/log-level")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String logConfiguration(@QueryParam("logger") @NotNull String logger,
                                   @QueryParam("level") @NotNull String level) {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(logger).setLevel(Level.valueOf(level));
        return String.format("Configured logging level for %s to %s", logger, level);
    }


}
