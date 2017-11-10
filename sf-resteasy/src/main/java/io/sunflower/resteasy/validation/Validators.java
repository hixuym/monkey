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

import io.sunflower.validation.BaseValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * A utility class for Hibernate.
 * @author michael
 */
public class Validators {
    private Validators() { /* singleton */ }

    /**
     * Creates a new {@link Validator} based on {@link #newValidatorFactory()}
     */
    public static Validator newValidator() {
        return newValidatorFactory().getValidator();
    }

    /**
     * Creates a new {@link ValidatorFactory} based on {@link #newConfiguration()}
     */
    public static ValidatorFactory newValidatorFactory() {
        return newConfiguration().buildValidatorFactory();
    }

    /**
     * Creates a new {@link HibernateValidatorConfiguration} with all the custom {@link
     * org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper} registered.
     */
    public static HibernateValidatorConfiguration newConfiguration() {
        return BaseValidator.newConfiguration()
                .parameterNameProvider(new ResteasyParameterNameProvider())
                .addValidatedValueHandler(new NonEmptyStringParamUnwrapper())
                .addValidatedValueHandler(new ParamValidatorUnwrapper());
    }
}
