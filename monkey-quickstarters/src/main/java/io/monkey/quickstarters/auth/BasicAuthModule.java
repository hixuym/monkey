package io.monkey.quickstarters.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.monkey.inject.advise.ProvidesWithAdvice;
import io.monkey.jaxrs.auth.Authenticator;
import io.monkey.jaxrs.auth.Authorizer;
import io.monkey.jaxrs.auth.PrincipalImpl;
import io.monkey.jaxrs.auth.UnauthorizedHandler;
import io.monkey.jaxrs.auth.basic.BasicCredentialAuthFilter;
import io.monkey.jaxrs.auth.basic.BasicCredentials;

import javax.inject.Singleton;

public class BasicAuthModule extends AbstractModule {

    private String realm = "Monkey";
    private String prefix = "Basic";

    public BasicAuthModule() {
    }

    public BasicAuthModule(String realm, String prefix) {
        this.realm = realm;
        this.prefix = prefix;
    }

    @Override
    protected void configure() {
    }

    @ProvidesWithAdvice
    @Singleton
    public Authenticator<BasicCredentials, PrincipalImpl> buildAuthenticator() {

        return new SimpleAuthenticator();
    }

    @Provides
    @Singleton
    public BasicCredentialAuthFilter<PrincipalImpl> buildAuthFilter(Authenticator<BasicCredentials, PrincipalImpl> authenticator,
                                                                    Authorizer<PrincipalImpl> authorizer,
                                                                    UnauthorizedHandler unauthorizedHandler) {
        return new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
                .setAuthenticator(authenticator)
                .setAuthorizer(authorizer)
                .setPrefix(this.prefix)
                .setRealm(this.realm)
                .setUnauthorizedHandler(unauthorizedHandler)
                .buildAuthFilter();
    }
}
