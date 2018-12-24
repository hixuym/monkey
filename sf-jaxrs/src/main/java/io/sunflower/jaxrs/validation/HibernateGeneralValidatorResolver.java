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

import org.jboss.resteasy.spi.validation.GeneralValidator;

import javax.validation.BootstrapConfiguration;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableType;
import javax.ws.rs.ext.ContextResolver;
import java.util.Set;

/**
 * HibernateGeneralValidatorResolver
 *
 * @author michael
 * created on 17/11/9 14:28
 */
class HibernateGeneralValidatorResolver implements ContextResolver<GeneralValidator> {

    private final static Object RD_LOCK = new Object();
    private volatile BootstrapConfiguration bootstrapConfiguration;
    private final Validator validator;

    public HibernateGeneralValidatorResolver(Validator validator) {
        this.validator = validator;
    }

    @Override
    public GeneralValidator getContext(Class<?> type) {
        BootstrapConfiguration bootstrapConfiguration = getConfig();
        boolean isExecutableValidationEnabled = bootstrapConfiguration.isExecutableValidationEnabled();
        Set<ExecutableType> defaultValidatedExecutableTypes = bootstrapConfiguration.getDefaultValidatedExecutableTypes();

        return new HibernateGeneralValidator(validator, isExecutableValidationEnabled, defaultValidatedExecutableTypes);
    }

    private BootstrapConfiguration getConfig() {
        BootstrapConfiguration tmpConfig = bootstrapConfiguration;
        if (tmpConfig == null) {
            synchronized (RD_LOCK) {
                tmpConfig = bootstrapConfiguration;
                if (tmpConfig == null) {
                    Configuration<?> config = Validation.byDefaultProvider().configure();
                    bootstrapConfiguration = tmpConfig = config.getBootstrapConfiguration();

                }
            }
        }
        return tmpConfig;
    }

}
