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

package io.sunflower.gizmo;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import io.sunflower.gizmo.diagnostics.DiagnosticError;
import io.sunflower.gizmo.diagnostics.DiagnosticErrorBuilder;
import io.sunflower.gizmo.exceptions.BadRequestException;
import io.sunflower.gizmo.exceptions.RenderingException;
import io.sunflower.gizmo.i18n.Messages;
import io.sunflower.gizmo.utils.GizmoConstant;
import io.sunflower.gizmo.utils.Message;
import io.sunflower.gizmo.utils.ResultHandler;
import io.sunflower.inject.lifecycle.LifecycleManager;

public class GizmoDefault implements Gizmo {
    private static final Logger logger = LoggerFactory.getLogger(GizmoDefault.class);

    @Inject
    protected LifecycleManager lifecycleManager;

    @Inject
    protected Router router;

    @Inject
    protected ResultHandler resultHandler;

    @Inject
    Messages messages;

    @Inject
    GizmoConfiguration configuration;

    /**
     * Whether diagnostics are enabled. If enabled then the default system/views will be skipped and a detailed
     * diagnostic error result will be returned by the various methods in this class. You get precise feedback where an
     * error occurred including original source code.
     *
     * @return True if diagnostics are enabled otherwise false.
     */
    public boolean isDiagnosticsEnabled() {
        // extra safety: only disable detailed diagnostic error pages
        // if both in DEV mode and diagnostics are enabled 0
        return configuration.isDiagnosticsEnabled();
    }


    @Override
    public void onRouteRequest(Context.Impl context) {

        String httpMethod = context.getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            Result underlyingResult = null;

            try {

                underlyingResult = route.getFilterChain().next(context);

                resultHandler.handleResult(underlyingResult, context);

            } catch (Exception exception) {

                // call special handler to capture the underlying result if there is one
                Result result = onException(context, exception, underlyingResult);
                renderErrorResultAndCatchAndLogExceptions(result, context);

            } finally {

                context.cleanup();

            }

        } else {

            // throw a 404 "not found" because we did not find the route
            Result result = getNotFoundResult(context);
            renderErrorResultAndCatchAndLogExceptions(result, context);

        }

    }

    @Override
    public void renderErrorResultAndCatchAndLogExceptions(
        Result result, Context context) {

        try {
            resultHandler.handleResult(result, context);
        } catch (Exception exceptionCausingRenderError) {
            logger.error("Unable to handle result. That's really really fishy.", exceptionCausingRenderError);
        }
    }

    @Override
    public void onFrameworkStart() {
        lifecycleManager.start();
    }

    @Override
    public void onFrameworkShutdown() {
        lifecycleManager.stop();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Results for exceptions (404, 500 etc)
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Result onException(Context context, Exception exception) {

        return onException(context, exception, null);

    }

    public Result onException(Context context, Exception exception, Result underlyingResult) {

        Result result;

        // log the exception as debug
        logger.debug("Unable to process request", exception);

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

    public Result getRenderingExceptionResult(Context context, RenderingException exception, Result underlyingResult) {

        if (isDiagnosticsEnabled()) {

            // prefer provided title and underlying cause
            DiagnosticError diagnosticError = DiagnosticErrorBuilder
                .buildDiagnosticError(
                    (exception.getTitle() == null ? "Rendering exception" : exception.getTitle()),
                    (exception.getCause() == null ? exception : exception.getCause()),
                    exception.getSourcePath(),
                    exception.getLineNumber(),
                    underlyingResult);

            return Results.internalServerError().render(diagnosticError);

        }

        return getInternalServerErrorResult(context, exception);

    }

    @Override
    public Result getInternalServerErrorResult(Context context, Exception exception) {
        return getInternalServerErrorResult(context, exception, null);
    }

    public Result getInternalServerErrorResult(Context context, Exception exception, Result underlyingResult) {

        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build500InternalServerErrorDiagnosticError(exception, true, underlyingResult);

            return Results.internalServerError().render(diagnosticError);

        }

        logger.error(
            "Emitting bad request 500. Something really wrong when calling route: {} (class: {} method: {})",
            context.getRequestPath(),
            context.getRoute().getControllerClass(),
            context.getRoute().getControllerMethod(),
            exception);

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT,
            context,
            Optional.<Result>empty());

        Message message = new Message(messageI18n);

        Result result = Results
            .internalServerError()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render(message);


        return result;
    }

    @Override
    public Result getNotFoundResult(Context context) {

        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build404NotFoundDiagnosticError(true);

            return Results.notFound().render(diagnosticError);

        }

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_NOT_FOUND_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_NOT_FOUND_TEXT_DEFAULT,
            context,
            Optional.<Result>empty());

        Message message = new Message(messageI18n);

        Result result = Results
            .notFound()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render(message);

        return result;

    }

    @Override
    public Result getBadRequestResult(Context context, Exception exception) {

        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build400BadRequestDiagnosticError(exception, true);

            return Results.badRequest().render(diagnosticError);

        }

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_BAD_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_BAD_REQUEST_TEXT_DEFAULT,
            context,
            Optional.<Result>empty());

        Message message = new Message(messageI18n);

        Result result = Results
            .badRequest()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render(message);

        return result;

    }

    @Override
    public Result getUnauthorizedResult(Context context) {

        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build401UnauthorizedDiagnosticError();

            return Results.unauthorized().render(diagnosticError);

        }

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_DEFAULT,
            context,
            Optional.<Result>empty());

        Message message = new Message(messageI18n);

        // WWW-Authenticate must be included per the spec
        // http://www.ietf.org/rfc/rfc2617.txt 3.2.1 The WWW-Authenticate Response Header
        Result result = Results
            .unauthorized()
            .addHeader(Result.WWW_AUTHENTICATE, "None")
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render(message);

        return result;

    }

    @Override
    public Result getForbiddenResult(Context context) {

        // diagnostic mode
        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build403ForbiddenDiagnosticError();

            return Results.forbidden().render(diagnosticError);

        }

        String messageI18n
            = messages.getWithDefault(
            GizmoConstant.I18N_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY,
            GizmoConstant.I18N_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT,
            context,
            Optional.<Result>empty());

        Message message = new Message(messageI18n);

        Result result = Results
            .forbidden()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render(message);

        return result;

    }
}
