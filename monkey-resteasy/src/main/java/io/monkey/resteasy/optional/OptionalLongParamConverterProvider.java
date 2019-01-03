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

package io.monkey.resteasy.optional;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.OptionalLong;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author michael
 */
@Singleton
public class OptionalLongParamConverterProvider implements ParamConverterProvider {
    private OptionalLongParamConverter paramConverter = new OptionalLongParamConverter();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType,
                                              final Annotation[] annotations) {
        return OptionalLong.class.equals(rawType) ? (ParamConverter<T>) paramConverter : null;
    }

    public static class OptionalLongParamConverter implements ParamConverter<OptionalLong> {
        @Override
        public OptionalLong fromString(final String value) {
            if (value == null) {
                return OptionalLong.empty();
            }

            try {
                return OptionalLong.of(Long.parseLong(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public String toString(final OptionalLong value) {
            checkArgument(value != null);
            return value.isPresent() ? Long.toString(value.getAsLong()) : "";
        }
    }
}
