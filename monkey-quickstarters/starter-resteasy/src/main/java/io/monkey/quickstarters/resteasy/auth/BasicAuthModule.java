/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.quickstarters.resteasy.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.monkey.inject.advise.ProvidesWithAdvice;
import io.monkey.resteasy.auth.Authenticator;
import io.monkey.resteasy.auth.Authorizer;
import io.monkey.resteasy.auth.PrincipalImpl;
import io.monkey.resteasy.auth.UnauthorizedHandler;
import io.monkey.resteasy.auth.basic.BasicCredentialAuthFilter;
import io.monkey.resteasy.auth.basic.BasicCredentials;

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
