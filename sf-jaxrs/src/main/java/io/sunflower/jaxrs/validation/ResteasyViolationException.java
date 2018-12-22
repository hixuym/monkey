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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author michael
 */
public class ResteasyViolationException extends ConstraintViolationException {

    private static final long serialVersionUID = -2084629736062306666L;
    private final Method method;
    private final Object target;

    public ResteasyViolationException(SimpleViolationsContainer container) {
        super(container.getViolations());

        this.target = container.getTarget();
        this.method = container.getMethod();
    }

    public ResteasyViolationException(Set<? extends ConstraintViolation<?>> constraintViolations,
                                      Object target,
                                      Method method) {
        super(constraintViolations);
        this.method = method;
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public Object getTarget() {
        return target;
    }
}
