/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.sunflower.gizmo.validation;

import com.google.inject.Inject;
import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.i18n.Lang;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Built in validators.
 *
 * @author James Roper
 * @author Thibault Meyer
 */
public class Validators {

  private static String fieldKey(String fieldName, String configuredFieldKey) {
    if (configuredFieldKey.length() > 0) {
      return configuredFieldKey;
    } else {
      return fieldName;
    }
  }

  /**
   * A basic Message interpolator.
   *
   * @author Thibault Meyer
   */
  private static class GizmoContextMsgInterpolator implements MessageInterpolator.Context,
      Serializable {

    private final Object value;
    private final ConstraintDescriptor<?> descriptor;

    /**
     * Create message interpolator context.
     *
     * @param value      value being validated
     * @param descriptor Constraint being validated
     */
    public GizmoContextMsgInterpolator(Object value, ConstraintDescriptor<?> descriptor) {
      this.value = value;
      this.descriptor = descriptor;
    }

    /**
     * Get the constraint descriptor.
     *
     * @return The constraint descriptor
     * @see javax.validation.metadata.ConstraintDescriptor
     */
    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
      return this.descriptor;
    }

    /**
     * Get the validated value.
     *
     * @return The value
     */
    @Override
    public Object getValidatedValue() {
      return this.value;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
      return null;
    }
  }

  public static class JSRValidator implements Validator<Object> {

    private final Lang requestLanguage;

    @Inject
    public JSRValidator(Lang requestLanguage) {
      this.requestLanguage = requestLanguage;
    }

    /**
     * Validate the given value.
     *
     * @param value   The value, may be null
     * @param field   The name of the field being validated, if applicable
     * @param context The Gizmo request context
     */
    @Override
    public void validate(Object value, String field, Context context) {
      if (value != null) {
        final ValidatorFactory validatorFactory = javax.validation.Validation
            .buildDefaultValidatorFactory();
        final javax.validation.Validator validator = validatorFactory.getValidator();
        final Set<javax.validation.ConstraintViolation<Object>> violations = validator
            .validate(value);
        final Locale localeToUse = this.requestLanguage.getLocaleFromStringOrDefault(
            this.requestLanguage.getLanguage(context, Optional.<Result>empty()));
        final Validation validation = context.getValidation();

        for (final javax.validation.ConstraintViolation<Object> violation : violations) {
          final String violationMessage = validatorFactory.getMessageInterpolator().interpolate(
              violation.getMessageTemplate(),
              new GizmoContextMsgInterpolator(value, violation.getConstraintDescriptor()),
              localeToUse
          );
          final String messageKey = violation.getMessageTemplate().replaceAll("[{}]", "");
          final ConstraintViolation constraintViolation = new ConstraintViolation(
              messageKey, violation.getPropertyPath().toString(), violationMessage,
              violation.getInvalidValue());
          validation.addViolation(constraintViolation);
        }
      }
    }

    @Override
    public Class<Object> getValidatedType() {
      return Object.class;
    }
  }

  public static class LengthValidator implements Validator<String> {

    private final Length length;

    public LengthValidator(Length length) {
      this.length = length;
    }

    /**
     * Validate the given value
     *
     * @param value   The value, may be null
     * @param field   The name of the field being validated, if applicable
     * @param context The Gizmo request context
     */
    @Override
    public void validate(String value, String field, Context context) {
      if (value != null) {
        if (this.length.max() != -1 && value.length() > this.length.max()) {
          context.getValidation().addViolation(
              new ConstraintViolation(this.length.maxKey(),
                  fieldKey(field, this.length.fieldKey()),
                  this.length.maxMessage(), this.length.max(), value));
        } else if (this.length.min() != -1 && value.length() < this.length.min()) {
          context.getValidation().addViolation(
              new ConstraintViolation(this.length.minKey(),
                  fieldKey(field, this.length.fieldKey()),
                  this.length.minMessage(), this.length.min(), value));
        }
      }
    }

    @Override
    public Class<String> getValidatedType() {
      return String.class;
    }
  }

  public static class MatchesValidator implements Validator<String> {

    private final Matches matches;
    private final Pattern pattern;

    public MatchesValidator(Matches matches) {
      this.matches = matches;
      this.pattern = Pattern.compile(matches.regexp());
    }

    /**
     * Validate the given value
     *
     * @param value   The value, may be null
     * @param field   The name of the field being validated, if applicable
     * @param context The Gizmo request context
     */
    @Override
    public void validate(String value, String field, Context context) {
      if (value != null) {
        if (!this.pattern.matcher(value).matches()) {
          context.getValidation().addViolation(
              new ConstraintViolation(
                  this.matches.key(),
                  fieldKey(field, this.matches.fieldKey()),
                  this.matches.message(),
                  this.matches.regexp(), value));
        }
      }
    }

    @Override
    public Class<String> getValidatedType() {
      return String.class;
    }
  }

  public static class NumberValidator implements Validator<Number> {

    private final NumberValue number;

    public NumberValidator(NumberValue number) {
      this.number = number;
    }

    /**
     * Validate the given value
     *
     * @param value   The value, may be null
     * @param field   The name of the field being validated, if applicable
     * @param context The Gizmo request context
     */
    @Override
    public void validate(Number value, String field, Context context) {
      if (value != null) {
        if (this.number.max() != Double.MAX_VALUE
            && value.doubleValue() > this.number.max()) {
          context.getValidation().addViolation(
              new ConstraintViolation(this.number.maxKey(),
                  fieldKey(field, this.number.fieldKey()),
                  this.number.maxMessage(), this.number.max(), value));
        } else if (this.number.min() != -1
            && value.doubleValue() < this.number.min()) {
          context.getValidation().addViolation(
              new ConstraintViolation(this.number.minKey(),
                  fieldKey(field, this.number.fieldKey()),
                  this.number.minMessage(), this.number.min(), value));
        }
      }
    }

    @Override
    public Class<Number> getValidatedType() {
      return Number.class;
    }
  }
}
