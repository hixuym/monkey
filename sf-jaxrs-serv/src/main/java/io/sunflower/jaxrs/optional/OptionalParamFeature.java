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

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * OptionalParamFeature
 *
 * @author michael
 * created on 17/11/9 11:18
 */
@Provider
public class OptionalParamFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {

        context.register(new EmptyOptionalExceptionMapper());
        context.register(new EmptyOptionalNoContentExceptionMapper());
        context.register(new OptionalDoubleMessageBodyWriter());
        context.register(new OptionalDoubleParamConverterProvider());
        context.register(new OptionalIntMessageBodyWriter());
        context.register(new OptionalIntParamConverterProvider());
        context.register(new OptionalLongMessageBodyWriter());
        context.register(new OptionalLongParamConverterProvider());
        context.register(new OptionalMessageBodyWriter());
        context.register(new OptionalParamConverterProvider());

        return true;
    }
}
