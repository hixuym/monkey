package io.sunflower.jaxrs.auth.basic;

import io.sunflower.jaxrs.auth.Authenticator;
import io.sunflower.jaxrs.auth.Authorizer;
import io.sunflower.jaxrs.auth.UnauthorizedHandler;

import javax.inject.Inject;
import javax.inject.Provider;
import java.security.Principal;

public class BasicCredentialAuthFilterProvider<P extends Principal> implements Provider<BasicCredentialAuthFilter<P>> {

    private String realm = "sunflower";
    private String prefix = "Basic";

    public BasicCredentialAuthFilterProvider() {
    }

    public BasicCredentialAuthFilterProvider(String realm, String prefix) {
        this.realm = realm;
        this.prefix = prefix;
    }

    @Inject
    private Authenticator<BasicCredentials, P> authenticator;

    @Inject
    private Authorizer<P> authorizer;

    @Inject
    private UnauthorizedHandler unauthorizedHandler;

    @Override
    public BasicCredentialAuthFilter<P> get() {

        return new BasicCredentialAuthFilter.Builder<P>()
                .setAuthenticator(authenticator)
                .setAuthorizer(authorizer)
                .setPrefix(this.realm)
                .setRealm(this.prefix)
                .setUnauthorizedHandler(unauthorizedHandler)
                .buildAuthFilter();

    }

}
