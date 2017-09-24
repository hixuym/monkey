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

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultExceptionHandler.class)
public interface ExceptionHandler {

    /**
     *
     * @param context
     * @param exception
     * @param underlyingResult
     * @return
     */
    Result onException(Context context, Exception exception, Result underlyingResult);

    /**
     * Should handle cases where no route can be found for a given request.
     *
     * Should lead to a html error 404 - not found (and be used with the same mindset).
     *
     * Usually used by onRouteRequest(...).
     */
    Result getNotFoundResult(Context context);

    /**
     * Should handle cases where access is unauthorized
     *
     * Should lead to a html error 401 - unauthorized (and be used with the same mindset).
     *
     * By default, WWW-Authenticate is set to None.
     *
     * Usually used by BasicAuthFilter for instance(...).
     */
    Result getUnauthorizedResult(Context context);

    /**
     * Should handle cases where access is forbidden
     *
     * Should lead to a html error 403 - forbidden (and be used with the same mindset).
     *
     * Usually used by SecureFilter for instance(...).
     */
    Result getForbiddenResult(Context context);


}
