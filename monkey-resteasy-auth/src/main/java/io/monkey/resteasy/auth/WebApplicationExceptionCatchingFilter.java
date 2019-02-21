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

package io.monkey.resteasy.auth;

import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * A {@link ContainerRequestFilter} decorator which catches any {@link
 * WebApplicationException WebApplicationExceptions} thrown by an
 * underlying {@code ContextRequestFilter}.
 */
@Priority(Priorities.AUTHENTICATION)
class WebApplicationExceptionCatchingFilter implements ContainerRequestFilter {
    private final ContainerRequestFilter underlying;

    public WebApplicationExceptionCatchingFilter(ContainerRequestFilter underlying) {
        Preconditions.checkNotNull(underlying, "Underlying ContainerRequestFilter is not set");
        this.underlying = underlying;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            underlying.filter(requestContext);
        } catch (WebApplicationException err) {
            // Pass through.
        }
    }
}
