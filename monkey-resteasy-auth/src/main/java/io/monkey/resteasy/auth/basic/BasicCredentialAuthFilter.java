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

package io.monkey.resteasy.auth.basic;

import com.google.common.io.BaseEncoding;
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
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class BasicCredentialAuthFilter<P extends Principal> extends AuthFilter<BasicCredentials, P> {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final BasicCredentials credentials =
                getCredentials(requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (!authenticate(requestContext, credentials, SecurityContext.BASIC_AUTH)) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
    }

    /**
     * Parses a Base64-encoded value of the `Authorization` header
     * in the form of `Basic dXNlcm5hbWU6cGFzc3dvcmQ=`.
     *
     * @param header the value of the `Authorization` header
     * @return a username and a password as {@link BasicCredentials}
     */
    @Nullable
    private BasicCredentials getCredentials(String header) {
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

        final String decoded;
        try {
            decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            logger.warn("Error decoding credentials", e);
            return null;
        }

        // Decoded credentials is 'username:password'
        final int i = decoded.indexOf(':');
        if (i <= 0) {
            return null;
        }

        final String username = decoded.substring(0, i);
        final String password = decoded.substring(i + 1);
        return new BasicCredentials(username, password);
    }

    /**
     * Builder for {@link BasicCredentialAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     *
     * @param <P> the principal
     */
    public static class Builder<P extends Principal> extends
            AuthFilterBuilder<BasicCredentials, P, BasicCredentialAuthFilter<P>> {

        @Override
        protected BasicCredentialAuthFilter<P> newInstance() {
            return new BasicCredentialAuthFilter<>();
        }
    }
}
