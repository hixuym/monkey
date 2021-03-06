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

import com.google.inject.TypeLiteral;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.inject.Singleton;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author michael
 */
@Singleton
public class OptionalParamConverterProvider implements ParamConverterProvider {

    private ResteasyProviderFactory providerFactory;

    public OptionalParamConverterProvider() {
        this.providerFactory = (ResteasyProviderFactory) RuntimeDelegate.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType,
                                              final Annotation[] annotations) {
        if (Optional.class.equals(rawType)) {

            TypeLiteral typeLiteral = TypeLiteral.get(genericType);

            if (typeLiteral.getRawType() == String.class) {
                return new ParamConverter<T>() {
                    @Override
                    public T fromString(final String value) {
                        return rawType.cast(Optional.ofNullable(value));
                    }

                    @Override
                    public String toString(final T value) {
                        return value.toString();
                    }
                };
            }

            final ParamConverter<?> converter = providerFactory.getParamConverter(typeLiteral.getRawType(), typeLiteral.getType(), annotations);

            if (converter != null) {
                return new ParamConverter<T>() {
                    @Override
                    public T fromString(final String value) {
                        return rawType.cast(Optional.ofNullable(value).map(s -> converter.fromString(value)));
                    }

                    @Override
                    public String toString(final T value) {
                        return value.toString();
                    }
                };
            }
        }

        return null;
    }
}
