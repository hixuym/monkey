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

package io.sunflower.validation.internal;

import io.sunflower.validation.ValidationMethod;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator for {@link ValidationMethod}-annotated methods.
 * @author michael
 */
public class MethodValidator implements ConstraintValidator<ValidationMethod, Boolean> {

    @Override
    public void initialize(ValidationMethod constraintAnnotation) {

    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        return (value == null) || value;
    }
}
