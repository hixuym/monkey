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

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * A {@link DynamicFeature} that registers the provided auth filter
 * to resource methods annotated with the {@link RolesAllowed}, {@link PermitAll}
 * and {@link DenyAll} annotations.
 * <p>In conjunction with {@link RolesAllowedDynamicFeature} it enables
 * authorization <i>AND</i> authentication of requests on the annotated methods.</p>
 * <p>If authorization is not a concern, then {@link RolesAllowedDynamicFeature}
 * could be omitted. But to enable authentication, the {@link PermitAll} annotation
 * should be placed on the corresponding resource methods.</p>
 * <p>Note that registration of the filter will follow the semantics of
 * {@link FeatureContext#register(Class)} and {@link FeatureContext#register(Object)}:
 * passing the filter as a {@link Class} to the {@link #AuthDynamicFeature(Class)}
 * constructor will result in dependency injection, while objects passed to
 * the {@link #AuthDynamicFeature(ContainerRequestFilter)} will be used directly.</p>
 */
@Provider
public class AuthDynamicFeature implements DynamicFeature {

    private final ContainerRequestFilter authFilter;

    private final Class<? extends ContainerRequestFilter> authFilterClass;

    // We suppress the null away checks, as adding `@Nullable` to the auth
    // filter fields, causes Jersey to try and resolve the fields to a concrete
    // type (which subsequently fails).
    @SuppressWarnings("NullAway")
    public AuthDynamicFeature(ContainerRequestFilter authFilter) {
        this.authFilter = authFilter;
        this.authFilterClass = null;
    }

    @SuppressWarnings("NullAway")
    public AuthDynamicFeature(Class<? extends ContainerRequestFilter> authFilterClass) {
        this.authFilter = null;
        this.authFilterClass = authFilterClass;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        final Method am = resourceInfo.getResourceMethod();
        final Annotation[][] parameterAnnotations = am.getParameterAnnotations();
        final Class<?>[] parameterTypes = am.getParameterTypes();

        // First, check for any @Auth annotations on the method.
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (final Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Auth) {
                    // Optional auth requires that a concrete AuthFilter be provided.
                    if (parameterTypes[i].equals(Optional.class) && authFilter != null) {
                        context.register(new WebApplicationExceptionCatchingFilter(authFilter));
                        return;
                    } else {
                        registerAuthFilter(context);
                        return;
                    }
                }
            }
        }

        // Second, check for any authorization annotations on the class or method.
        // Note that @DenyAll shouldn't be attached to classes.
        final boolean annotationOnClass = (resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class) != null) ||
            (resourceInfo.getResourceClass().getAnnotation(PermitAll.class) != null);
        final boolean annotationOnMethod = am.isAnnotationPresent(RolesAllowed.class) || am.isAnnotationPresent(DenyAll.class) ||
            am.isAnnotationPresent(PermitAll.class);

        if (annotationOnClass || annotationOnMethod) {
            registerAuthFilter(context);
        }
    }

    private void registerAuthFilter(FeatureContext context) {
        if (authFilter != null) {
            context.register(authFilter);
        } else if (authFilterClass != null) {
            context.register(authFilterClass);
        }
    }
}
