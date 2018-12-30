package io.monkey.jaxrs.auth;

import com.google.inject.ImplementedBy;

import javax.ws.rs.core.Response;

@ImplementedBy(DefaultUnauthorizedHandler.class)
public interface UnauthorizedHandler {
    Response buildResponse(String prefix, String realm);
}
