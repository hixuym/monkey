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

package io.monkey.resteasy.auth;

import java.security.Principal;
import java.util.Optional;

/**
 * An interface for classes which authenticate user-provided credentials and return principal
 * objects.
 *
 * @param <C> the type of credentials the authenticator can authenticate
 * @param <P> the type of principals the authenticator returns
 */
public interface Authenticator<C, P extends Principal> {
    /**
     * Given a set of user-provided credentials, return an optional principal.
     *
     * If the credentials are valid and map to a principal, returns an {@link Optional#of(Object)}.
     *
     * If the credentials are invalid, returns an {@link Optional#empty()}.
     *
     * @param credentials a set of user-provided credentials
     * @return either an authenticated principal or an absent optional
     * @throws AuthenticationException if the credentials cannot be authenticated due to an
     *                                 underlying error
     */
    Optional<P> authenticate(C credentials) throws AuthenticationException;
}
