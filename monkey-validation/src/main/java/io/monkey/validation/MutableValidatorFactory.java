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

package io.monkey.validation;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

public class MutableValidatorFactory implements ConstraintValidatorFactory {

    private ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactoryImpl();

    @Override
    public final <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return validatorFactory.getInstance(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) { }

    public void setValidatorFactory(ConstraintValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }
}
