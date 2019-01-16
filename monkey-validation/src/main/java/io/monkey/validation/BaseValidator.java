package io.monkey.validation;

import io.monkey.validation.valuehandling.OptionalDoubleValidatedValueUnwrapper;
import io.monkey.validation.valuehandling.OptionalIntValidatedValueUnwrapper;
import io.monkey.validation.valuehandling.OptionalLongValidatedValueUnwrapper;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.Validation;
import javax.validation.Validator;

public class BaseValidator {
    private BaseValidator() { /* singleton */ }

    /**
     * Creates a new {@link Validator} based on {@link #newConfiguration()}
     */
    public static Validator newValidator() {
        return newConfiguration().buildValidatorFactory().getValidator();
    }

    /**
     * Creates a new {@link HibernateValidatorConfiguration} with the base custom {@link
     * org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper} registered.
     */
    public static HibernateValidatorConfiguration newConfiguration() {
        return Validation
                .byProvider(HibernateValidator.class)
                .configure()
                .constraintValidatorFactory(new MutableValidatorFactory())
                .addValidatedValueHandler(new OptionalDoubleValidatedValueUnwrapper())
                .addValidatedValueHandler(new OptionalIntValidatedValueUnwrapper())
                .addValidatedValueHandler(new OptionalLongValidatedValueUnwrapper());
    }
}
