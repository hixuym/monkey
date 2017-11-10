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

package io.sunflower.resteasy.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author michael
 */
@Provider
public abstract class LoggingExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingExceptionMapper.class);

    @Override
    public Response toResponse(E exception) {
        // If we're dealing with a web exception, we can service certain types of request (like
        // redirection or server errors) better and also propagate properties of the inner response.
        if (exception instanceof WebApplicationException) {
            final Response response = ((WebApplicationException) exception).getResponse();
            Response.Status.Family family = response.getStatusInfo().getFamily();
            if (family.equals(Response.Status.Family.REDIRECTION)) {
                return response;
            }
            if (family.equals(Response.Status.Family.SERVER_ERROR)) {
                logException(exception);
            }

            return Response.fromResponse(response)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(response.getStatus(), exception.getLocalizedMessage()))
                .build();
        }

        // Else the thrown exception is a not a web exception, so the exception is most likely
        // unexpected. We'll create a unique id in the server error response that is also logged for
        // correlation
        final long id = logException(exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(formatErrorMessage(id, exception)))
                .build();
    }

    @SuppressWarnings("UnusedParameters")
    protected String formatErrorMessage(long id, E exception) {
        return String.format("There was an error processing your request. It has been logged (ID %016x).", id);
    }

    protected long logException(E exception) {
        final long id = ThreadLocalRandom.current().nextLong();
        logException(id, exception);
        return id;
    }

    protected void logException(long id, E exception) {
        LOGGER.error(formatLogMessage(id, exception), exception);
    }

    @SuppressWarnings("UnusedParameters")
    protected String formatLogMessage(long id, Throwable exception) {
        return String.format("Error handling a request: %016x", id);
    }
}
