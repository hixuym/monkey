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

package io.sunflower.jaxrs.params;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.sunflower.jaxrs.errors.ErrorMessage;
import io.sunflower.util.Enums;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static io.sunflower.jaxrs.params.ParameterNameProvider.getParameterNameFromAnnotations;

/**
 * Provides converters to jersey for enums used as resource parameters.
 *
 * <p>By default jersey will return a 404 if a resource parameter of an enum type cannot be converted. This class
 * provides converters for all enum types used as resource parameters that provide better error handling. If an
 * invalid value is provided for the parameter a {@code 400 Bad Request} is returned and the error message will
 * include the parameter name and a list of valid values.</p>
 * @author michael
 */
@SuppressWarnings("unchecked")
@Provider
public class FuzzyEnumParamConverterProvider implements ParamConverterProvider {

    private final static Joiner JOINER = Joiner.on(", ");

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (!rawType.isEnum()) {
            return null;
        }

        final Class<Enum<?>> type = (Class<Enum<?>>) rawType;
        final Enum<?>[] constants = type.getEnumConstants();
        final String parameterName = getParameterNameFromAnnotations(annotations).orElse("Parameter");

        return new ParamConverter<T>() {
            @Override
            public T fromString(String value) {
                if (Strings.isNullOrEmpty(value)) {
                    return null;
                }

                final Enum<?> constant = Enums.fromStringFuzzy(value, constants);
                if (constant != null) {
                    return (T) constant;
                }

                final String errMsg = String.format("%s must be one of [%s]", parameterName, JOINER.join(constants));
                throw new WebApplicationException(getErrorResponse(errMsg));
            }

            @Override
            public String toString(T value) {
                return value.toString();
            }

            protected Response getErrorResponse(String message) {
                return Response
                    .status(400)
                    .entity(new ErrorMessage(400, message))
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
            }
        };
    }

}
