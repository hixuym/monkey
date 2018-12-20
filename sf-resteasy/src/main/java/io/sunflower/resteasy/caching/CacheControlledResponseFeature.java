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

package io.sunflower.resteasy.caching;

import javax.ws.rs.container.*;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author michael
 */
@Provider
public class CacheControlledResponseFeature implements DynamicFeature {

    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext featureContext) {
        final Method resourceMethod = resourceInfo.getResourceMethod();

        // check to see if it has cache control annotation

        if (resourceMethod.isAnnotationPresent(CacheControl.class)) {
            CacheControl cc = resourceMethod.getAnnotation(CacheControl.class);
            featureContext.register(new CacheControlledResponseFilter(cc));
        }
    }

    private static class CacheControlledResponseFilter implements ContainerResponseFilter {
        private static final int ONE_YEAR_IN_SECONDS = (int) TimeUnit.DAYS.toSeconds(365);
        private String cacheResponseHeader;

        CacheControlledResponseFilter(CacheControl control) {
            final javax.ws.rs.core.CacheControl cacheControl = new javax.ws.rs.core.CacheControl();
            cacheControl.setPrivate(control.isPrivate());
            cacheControl.setNoCache(control.noCache());
            cacheControl.setNoStore(control.noStore());
            cacheControl.setNoTransform(control.noTransform());
            cacheControl.setMustRevalidate(control.mustRevalidate());
            cacheControl.setProxyRevalidate(control.proxyRevalidate());
            cacheControl.setMaxAge((int) control.maxAgeUnit().toSeconds(control.maxAge()));
            cacheControl.setSMaxAge((int) control.sharedMaxAgeUnit().toSeconds(control.sharedMaxAge()));
            if (control.immutable()) {
                cacheControl.setMaxAge(ONE_YEAR_IN_SECONDS);
            }

            cacheResponseHeader = cacheControl.toString();
        }

        @Override
        public void filter(ContainerRequestContext requestContext,
                           ContainerResponseContext responseContext) {
            if (!cacheResponseHeader.isEmpty()) {
                responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, cacheResponseHeader);
            }

        }

    }
}
