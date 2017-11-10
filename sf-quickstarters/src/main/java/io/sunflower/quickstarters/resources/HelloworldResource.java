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

package io.sunflower.quickstarters.resources;

import io.sunflower.resteasy.params.IntParam;
import io.sunflower.quickstarters.core.GreetingService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

/**
 * HelloworldResource
 *
 * @author michael
 * created on 17/11/8 11:17
 */
@Singleton
@Path("/helloworld")
public class HelloworldResource {

    private final GreetingService greetingService;

    @Inject
    public HelloworldResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GET
    @Path("/intparam/{id}")
    public Optional<String> intparam(@PathParam("id") IntParam id) {
        return Optional.of(id.toString());
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Message helloworld(@Size(min = 8) @PathParam("name") String name) {

        Message message = new Message();

        message.content = greetingService.greeting(name);

        return message;
    }

    public static class Message {
        public String content;
    }
}
