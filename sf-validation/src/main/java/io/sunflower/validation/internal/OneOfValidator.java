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

import io.sunflower.validation.OneOf;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author michael
 */
public class OneOfValidator implements ConstraintValidator<OneOf, Object> {

    private String[] values;
    private boolean caseInsensitive;
    private boolean ignoreWhitespace;

    @Override
    public void initialize(OneOf constraintAnnotation) {
        this.values = constraintAnnotation.value();
        this.caseInsensitive = constraintAnnotation.ignoreCase();
        this.ignoreWhitespace = constraintAnnotation.ignoreWhitespace();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        final String v = ignoreWhitespace ? value.toString().trim() : value.toString();
        if (caseInsensitive) {
            for (String s : values) {
                if (s.equalsIgnoreCase(v)) {
                    return true;
                }
            }
        } else {
            for (String s : values) {
                if (s.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }
}
