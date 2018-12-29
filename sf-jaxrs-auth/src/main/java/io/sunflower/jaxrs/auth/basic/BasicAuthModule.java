package io.sunflower.jaxrs.auth.basic;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.sunflower.jaxrs.auth.Authenticator;
import io.sunflower.jaxrs.auth.Authorizer;
import io.sunflower.jaxrs.auth.UnauthorizedHandler;

import javax.inject.Singleton;
import java.security.Principal;

public abstract class BasicAuthModule extends AbstractModule {

    private String realm = "sunflower";
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

    abstract void configureAuthenticator();

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
