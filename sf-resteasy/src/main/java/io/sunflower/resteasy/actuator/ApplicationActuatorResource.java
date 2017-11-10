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

package io.sunflower.resteasy.actuator;

import io.sunflower.resteasy.params.IntParam;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * ApplicationActuatorResource
 *
 * @author michael
 * created on 17/11/10 22:30
 */
@Singleton
@Path("/actuator")
public class ApplicationActuatorResource {

    @Path("/metrics")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String metrics() {
        return "";
    }

    @Path("/healthcheck")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String healthCheck() {
        return "";
    }

    @Path("/gc")
    @POST
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
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String threadDump() {

        return "";
    }

    @Path("/log-level")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String logConfiguration() {

        return "";
    }


}
