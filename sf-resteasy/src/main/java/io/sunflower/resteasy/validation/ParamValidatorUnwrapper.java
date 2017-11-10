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

package io.sunflower.resteasy.validation;


import com.fasterxml.classmate.TypeResolver;
import io.sunflower.resteasy.params.AbstractParam;
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;

import java.lang.reflect.Type;

/**
 * Let's the validator know that when validating a class that is an {@link AbstractParam} to
 * validate the underlying value.
 * @author michael
 */
public class ParamValidatorUnwrapper extends ValidatedValueUnwrapper<AbstractParam<?>> {
    private final TypeResolver resolver = new TypeResolver();

    @Override
    public Object handleValidatedValue(final AbstractParam<?> abstractParam) {
        return abstractParam == null ? null : abstractParam.get();
    }

    @Override
    public Type getValidatedValueType(final Type type) {
        return resolver.resolve(type)
                .typeParametersFor(AbstractParam.class).get(0)
                .getErasedType();
    }
}
