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

import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author michael
 */
@Provider
@Produces(MediaType.WILDCARD)
public class OptionalMessageBodyWriter implements MessageBodyWriter<Optional<?>> {

    @Context
    private Providers providerFactory;

    // Jersey ignores this
    @Override
    public long getSize(Optional<?> entity, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return Optional.class.isAssignableFrom(type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void writeTo(Optional<?> entity,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException {
        if (!entity.isPresent()) {
            throw EmptyOptionalException.INSTANCE;
        }

        final Type innerGenericType = (genericType instanceof ParameterizedType) ?
                ((ParameterizedType) genericType).getActualTypeArguments()[0] : entity.get().getClass();

        final MessageBodyWriter writer = providerFactory.getMessageBodyWriter(entity.get().getClass(),
                innerGenericType, annotations, mediaType);
        writer.writeTo(entity.get(), entity.get().getClass(),
                innerGenericType, annotations, mediaType, httpHeaders, entityStream);
    }

}
