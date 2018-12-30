package io.monkey.jaxrs.auth.basic;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.monkey.jaxrs.auth.Authenticator;
import io.monkey.jaxrs.auth.Authorizer;
import io.monkey.jaxrs.auth.UnauthorizedHandler;

import javax.inject.Singleton;
import java.security.Principal;

public abstract class BasicAuthModule extends AbstractModule {

    private String realm = "monkey";
    private String prefix = "Basic";

    public BasicAuthModule() {
    }

    public BasicAuthModule(String realm, String prefix) {
        this.realm = realm;
        this.prefix = prefix;
    }

    @Override
    protected void configure() {
        configureAuthenticator();
    }

    protected abstract void configureAuthenticator();

    @Provides
    @Singleton
    public <P extends Principal> BasicCredentialAuthFilter<P> buildAuthFilter(Authenticator<BasicCredentials, P> authenticator,
                                                                         Authorizer<P> authorizer,
                                                                         UnauthorizedHandler unauthorizedHandler) {
        return new BasicCredentialAuthFilter.Builder<P>()
                .setAuthenticator(authenticator)
                .setAuthorizer(authorizer)
                .setPrefix(this.realm)
                .setRealm(this.prefix)
                .setUnauthorizedHandler(unauthorizedHandler)
                .buildAuthFilter();
    }
}
