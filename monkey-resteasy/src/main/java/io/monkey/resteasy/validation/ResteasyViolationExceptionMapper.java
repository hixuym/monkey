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

package io.monkey.resteasy.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author michael
 */
@Provider
class ResteasyViolationExceptionMapper implements ExceptionMapper<ResteasyViolationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResteasyViolationExceptionMapper.class);

    @Override
    public Response toResponse(final ResteasyViolationException exception) {

        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final Method invocable = exception.getInvocable();
        final List<String> errors = violations.stream()
                .map(violation -> ConstraintMessage.getMessage(violation, invocable))
                .collect(Collectors.toList());

        final int status = ConstraintMessage.determineStatus(violations, invocable);
        return Response.status(status)
                .entity(new ValidationErrorMessage(errors))
                .build();
    }
}
