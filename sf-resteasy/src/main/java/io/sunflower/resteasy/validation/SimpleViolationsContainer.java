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

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author michael
 */
public class SimpleViolationsContainer implements Serializable {
    private static final long serialVersionUID = -7895854137980651539L;

    private Set<ConstraintViolation<Object>> violations = new HashSet<>();
    private Exception exception;
    private Object target;
    private Method method;

    public SimpleViolationsContainer(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public SimpleViolationsContainer(Set<ConstraintViolation<Object>> cvs) {
        violations.addAll(cvs);
    }

    public void addViolations(Set<ConstraintViolation<Object>> cvs) {
        violations.addAll(cvs);
    }

    public int size() {
        return violations.size();
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }
}
