/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.resteasy.auth.oauth;

import io.monkey.resteasy.auth.AuthFilter;
import io.monkey.resteasy.auth.Authenticator;

import javax.annotation.Nullable;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION)
@Provider
public class OAuthCredentialAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    /**
     * Query parameter used to pass Bearer token
     *
     * @see <a href="https://tools.ietf.org/html/rfc6750#section-2.3">The OAuth 2.0 Authorization Framework: Bearer Token Usage</a>
     */
    public static final String OAUTH_ACCESS_TOKEN_PARAM = "access_token";

    private OAuthCredentialAuthFilter() {
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        String credentials = getCredentials(requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        // If Authorization header is not used, check query parameter where token can be passed as well
        if (credentials == null) {
            credentials = requestContext.getUriInfo().getQueryParameters().getFirst(OAUTH_ACCESS_TOKEN_PARAM);
        }

        if (!authenticate(requestContext, credentials, SecurityContext.BASIC_AUTH)) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
    }

    /**
     * Parses a value of the `Authorization` header in the form of `Bearer a892bf3e284da9bb40648ab10`.
     *
     * @param header the value of the `Authorization` header
     * @return a token
     */
    @Nullable
    private String getCredentials(String header) {
        if (header == null) {
            return null;
        }

        final int space = header.indexOf(' ');
        if (space <= 0) {
            return null;
        }

        final String method = header.substring(0, space);
        if (!prefix.equalsIgnoreCase(method)) {
            return null;
        }

        return header.substring(space + 1);
    }

    /**
     * Builder for {@link OAuthCredentialAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     *
     * @param <P> the type of the principal
     */
    public static class Builder<P extends Principal>
            extends AuthFilterBuilder<String, P, OAuthCredentialAuthFilter<P>> {

        @Override
        protected OAuthCredentialAuthFilter<P> newInstance() {
            return new OAuthCredentialAuthFilter<>();
        }
    }
}
