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

package io.monkey.jaxrs.params;

import com.google.common.base.Strings;
import io.monkey.jaxrs.validation.ResteasyParameterNameProvider;

import javax.annotation.Nullable;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Provides converters to resteasy for monkey's *Param classes.
 * <p>
 * <p>When a param class is used as a resource parameter this converter will instantiate the parameter class with the
 * value provided and the name of the parameter, so if value parsing fails the parameter name can be used in the error
 * message. If the param class does not have a two-string constructor this provider will return null, causing jersey
 * to use the single-string constructor for the parameter type as it normally would.</p>
 *
 * @author michael
 */
@Provider
public class AbstractParamConverterProvider implements ParamConverterProvider {

    public AbstractParamConverterProvider() {
    }

    @Override
    @Nullable
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (AbstractParam.class.isAssignableFrom(rawType)) {
            final String parameterName = ResteasyParameterNameProvider.getParameterNameFromAnnotations(annotations).orElse("Parameter");
            final Constructor<T> constructor;
            try {
                constructor = rawType.getConstructor(String.class, String.class);
            } catch (NoSuchMethodException ignored) {
                // The Param class did not have a (String, String) constructor. We return null,
                // leaving Jersey to handle these parameters as it normally would.
                return null;
            }
            return new ParamConverter<T>() {
                @Override
                @Nullable
                public T fromString(String value) {
                    if (rawType != NonEmptyStringParam.class && Strings.isNullOrEmpty(value)) {
                        return null;
                    }
                    try {
                        return _fromString(value);
                    } catch (InvocationTargetException ex) {
                        final Throwable cause = ex.getCause();
                        if (cause instanceof WebApplicationException) {
                            throw (WebApplicationException) cause;
                        } else {
                            throw new WebApplicationException(cause);
                        }
                    } catch (final Exception ex) {
                        throw new ProcessingException(ex);
                    }
                }

                protected T _fromString(String value) throws Exception {
                    return constructor.newInstance(value, parameterName);
                }

                @Override
                public String toString(T value) throws IllegalArgumentException {
                    if (value == null) {
                        throw new IllegalArgumentException("METHOD_PARAMETER_CANNOT_BE_NULL");
                    }
                    return value.toString();
                }

            };
        }
        return null;
    }
}
