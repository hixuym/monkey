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

package io.sunflower.gizmo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javax.inject.Inject;

import io.sunflower.gizmo.exceptions.BadRequestException;
import io.sunflower.gizmo.exceptions.RenderingException;
import io.sunflower.gizmo.i18n.Messages;
import io.sunflower.gizmo.utils.ErrorMessage;
import io.sunflower.gizmo.utils.GizmoConstant;

public class DefaultExceptionHandler implements ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    private final Messages messages;
    private final GizmoConfiguration configuration;

    @Inject
    public DefaultExceptionHandler(Messages messages, GizmoConfiguration configuration) {
        this.messages = messages;
        this.configuration = configuration;
    }

    /**
     * Whether diagnostics are enabled. If enabled then the default system/views will be skipped and a detailed
     * diagnostic error result will be returned by the various methods in this class. You get precise feedback where an
     * error occurred including original source code.
     *
     * @return True if diagnostics are enabled otherwise false.
     */
    protected boolean isDiagnosticsEnabled() {
        // extra safety: only disable detailed diagnostic error pages
        // if both in DEV mode and diagnostics are enabled 0
        return configuration.isDev() && configuration.isDiagnosticsEnabled();
    }

    @Override
    public Result onException(Context context, Exception exception, Result underlyingResult) {

        Result result;
        // log the exception as debug
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to process request", exception);
        }

        if (exception instanceof BadRequestException) {
            result = getBadRequestResult(context, exception);
        } else if (exception instanceof RenderingException) {
            RenderingException renderingException = (RenderingException) exception;
            result = getRenderingExceptionResult(context, renderingException, underlyingResult);
        } else {
            result = getInternalServerErrorResult(context, exception, underlyingResult);
        }
        return result;
    }

    @Override
    public Result getNotFoundResult(Context context) {

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_NOT_FOUND_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_NOT_FOUND_TEXT_DEFAULT,
            context,
            Optional.empty());

        ErrorMessage message = new ErrorMessage(Result.SC_404_NOT_FOUND, messageI18n);

        return Results
            .notFound().json()
            .render(message);
    }

    @Override
    public Result getUnauthorizedResult(Context context) {

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_DEFAULT,
            context,
            Optional.empty());

        ErrorMessage message = new ErrorMessage(Result.SC_401_UNAUTHORIZED, messageI18n);

        // WWW-Authenticate must be included per the spec
        // http://www.ietf.org/rfc/rfc2617.txt 3.2.1 The WWW-Authenticate Response Header

        return Results
            .unauthorized().json()
            .addHeader(Result.WWW_AUTHENTICATE, "None")
            .render(message);
    }

    @Override
    public Result getForbiddenResult(Context context) {

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT,
            context,
            Optional.empty());

        ErrorMessage message = new ErrorMessage(Result.SC_403_FORBIDDEN, messageI18n);

        return Results.forbidden().json().render(message);

    }

    private Result getBadRequestResult(Context context, Exception exception) {

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_BAD_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_BAD_REQUEST_TEXT_DEFAULT,
            context,
            Optional.empty());

        ErrorMessage message = new ErrorMessage(Result.SC_400_BAD_REQUEST, messageI18n, exception.getMessage());

        return Results
            .badRequest().json()
            .render(message);
    }

    private Result getRenderingExceptionResult(Context context, RenderingException exception, Result underlyingResult) {

        return getInternalServerErrorResult(context, exception);

    }

    private Result getInternalServerErrorResult(Context context, Exception exception) {
        return getInternalServerErrorResult(context, exception, null);
    }

    private Result getInternalServerErrorResult(Context context, Exception exception, Result underlyingResult) {

        logger.error(
            "Emitting bad request 500. Something really wrong when calling route: {} (class: {} method: {})",
            context.getRequestPath(),
            context.getRoute().getResourceClass(),
            context.getRoute().getResourceMethod(),
            exception);

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT,
            context,
            Optional.empty());

        ErrorMessage message = new ErrorMessage(Result.SC_500_INTERNAL_SERVER_ERROR, messageI18n, exception.getMessage());

        return Results
            .internalServerError()
            .json()
            .render(message);
    }

}
