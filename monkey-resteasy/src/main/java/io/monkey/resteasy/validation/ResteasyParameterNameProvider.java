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

package io.monkey.resteasy.validation;

import org.hibernate.validator.parameternameprovider.ReflectionParameterNameProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adds resteasy support to parameter name discovery in hibernate validator.
 *
 * <p>This provider will behave like the hibernate-provided {@link ReflectionParameterNameProvider} except when a
 * method parameter is annotated with a jersey parameter annotation, like {@link QueryParam}. If a resteasy parameter
 * annotation is present the value of the annotation is used as the parameter name.</p>
 * @author michael
 */
public class ResteasyParameterNameProvider extends ReflectionParameterNameProvider {

    @Override
    public List<String> getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<String> names = new ArrayList<>(parameterAnnotations.length);
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            String name = getParameterNameFromAnnotations(annotations).orElse(parameters[i].getName());
            names.add(name);
        }
        return names;
    }

    /**
     * Derives member's name and type from it's annotations
     */
    public static Optional<String> getParameterNameFromAnnotations(Annotation[] memberAnnotations) {
        for (Annotation a : memberAnnotations) {
            if (a instanceof QueryParam) {
                return Optional.of("query param " + ((QueryParam) a).value());
            } else if (a instanceof PathParam) {
                return Optional.of("path param " + ((PathParam) a).value());
            } else if (a instanceof HeaderParam) {
                return Optional.of("header " + ((HeaderParam) a).value());
            } else if (a instanceof CookieParam) {
                return Optional.of("cookie " + ((CookieParam) a).value());
            } else if (a instanceof FormParam) {
                return Optional.of("form field " + ((FormParam) a).value());
            } else if (a instanceof Context) {
                return Optional.of("context");
            } else if (a instanceof MatrixParam) {
                return Optional.of("matrix param " + ((MatrixParam) a).value());
            }
        }

        return Optional.empty();
    }

}
