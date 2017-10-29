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

import io.sunflower.util.Duration;
import io.sunflower.validation.MinDuration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.concurrent.TimeUnit;

/**
 * Check that a {@link Duration} being validated is greater than or equal to the minimum value
 * specified.
 * @author michael
 */
public class MinDurationValidator implements ConstraintValidator<MinDuration, Duration> {

    private long minQty;
    private TimeUnit minUnit;

    @Override
    public void initialize(MinDuration constraintAnnotation) {
        this.minQty = constraintAnnotation.value();
        this.minUnit = constraintAnnotation.unit();
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        return (value == null) || (value.toNanoseconds() >= minUnit.toNanos(minQty));
    }
}
