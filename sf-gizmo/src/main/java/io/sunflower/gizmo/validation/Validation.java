/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.validation;

import com.google.inject.ImplementedBy;

import java.util.List;

/**
 * This interface means the validation context (implemented by {@link ValidationImpl}) and can be injected in your
 * controller method.
 * There are several types of violations that can occur: field violations (on controller method fields), bean violations
 * (on an injected beans field) or general violations (deprecated). A controller using this validation can have
 * violations on his
 * parameters or, if you use a injected data container like a DTO or bean, you may have violations inside this object.
 * possible to validate all controller parameters at once. If an error appears while validating the controller
 * method parameters, it results in a violation which you can get using getFieldViolations().
 * If your injected bean contains violations, you should use getBeanViolations().
 *
 * @author James Roper, Philip Sommer
 */
@ImplementedBy(ValidationImpl.class)
public interface Validation {
    /**
     * Whether the validation context has violations (including field and bean violations)
     *
     * @return True if it does
     */
    boolean hasViolations();

    /**
     * Whether the validation context has a violation for the given field
     *
     * @return True if it does
     */
    boolean hasViolation(String paramName);

    /**
     * Get all constraint violations.
     *
     * @return The list of all violations.
     */
    List<ConstraintViolation> getViolations();

    /**
     * Get a complete list of field violations for a specified parameter.
     *
     * @return A List of FieldViolation-objects
     */
    List<ConstraintViolation> getViolations(String paramName);

    /**
     * Add a violation
     *
     * @param constraintViolation
     *            The constraint violation
     */
    void addViolation(ConstraintViolation constraintViolation);
}
