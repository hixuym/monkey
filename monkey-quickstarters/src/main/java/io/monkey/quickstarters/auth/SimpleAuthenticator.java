package io.monkey.quickstarters.auth;

import io.monkey.jaxrs.auth.Authenticator;
import io.monkey.jaxrs.auth.PrincipalImpl;
import io.monkey.jaxrs.auth.basic.BasicCredentials;

import java.util.Optional;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {
    @Override
    public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) {

        if ("admin".equalsIgnoreCase(credentials.getUsername())
            && "123456".equalsIgnoreCase(credentials.getPassword())) {
            return Optional.of(new PrincipalImpl("admin"));
        }

        return Optional.empty();
    }
}
