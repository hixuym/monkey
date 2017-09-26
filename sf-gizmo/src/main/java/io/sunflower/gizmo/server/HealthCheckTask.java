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


import com.google.common.collect.ImmutableMultimap;
import com.google.common.util.concurrent.MoreExecutors;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.json.HealthCheckModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.PrintWriter;
import java.util.SortedMap;

import io.sunflower.jackson.Jackson;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.Task;

public class HealthCheckTask extends Task {

    private final Environment environment;

    private final ObjectMapper mapper = Jackson.newObjectMapper().registerModule(new HealthCheckModule());

    /**
     * Create a new task with the given name.
     *
     * @param environment the app environment
     */
    protected HealthCheckTask(Environment environment) {
        super("healthcheck");
        this.environment = environment;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        final SortedMap<String, HealthCheck.Result> results = runHealthChecks();
        getWriter().writeValue(output, results);
        output.flush();
    }

    private SortedMap<String, HealthCheck.Result> runHealthChecks() {
        return environment.healthChecks().runHealthChecks(MoreExecutors.newDirectExecutorService());
    }

    private ObjectWriter getWriter() {
        return mapper.writerWithDefaultPrettyPrinter();
    }
}
