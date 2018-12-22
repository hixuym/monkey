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

package io.sunflower.jaxrs.optional;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.OptionalInt;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author michael
 */
@Singleton
public class OptionalIntParamConverterProvider implements ParamConverterProvider {
    private final OptionalIntParamConverter paramConverter = new OptionalIntParamConverter();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType,
                                              final Annotation[] annotations) {
        return OptionalInt.class.equals(rawType) ? (ParamConverter<T>) paramConverter : null;
    }

    public static class OptionalIntParamConverter implements ParamConverter<OptionalInt> {
        @Override
        public OptionalInt fromString(final String value) {
            if (value == null) {
                return OptionalInt.empty();
            }

            try {
                return OptionalInt.of(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public String toString(final OptionalInt value) {
            checkArgument(value != null);
            return value.isPresent() ? Integer.toString(value.getAsInt()) : "";
        }
    }
}
