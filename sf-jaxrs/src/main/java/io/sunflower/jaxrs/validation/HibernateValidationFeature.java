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

package io.sunflower.jaxrs.validation;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.validation.Validator;
import javax.ws.rs.ext.Provider;

/**
 * HibernateValidationFeature
 *
 * @author michael
 * created on 17/11/9 16:24
 */
@Provider
public class HibernateValidationFeature implements Feature {

    private final Validator validator;

    @Inject
    public HibernateValidationFeature(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean configure(FeatureContext context) {
        context.register(new HibernateGeneralValidatorResolver(validator));
        context.register(new ResteasyViolationExceptionMapper());
        return true;
    }
}