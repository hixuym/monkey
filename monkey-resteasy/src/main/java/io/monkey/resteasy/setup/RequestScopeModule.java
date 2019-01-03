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

package io.monkey.resteasy.setup;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.*;

/**
 * Binds the {@link RequestScoped} to the current HTTP request and
 * makes all the classes available via the {@link Context} annotation injectable.
 *
 * @author michael
 */
public class RequestScopeModule extends AbstractModule {
    @Override
    protected void configure() {
        bindScope(RequestScoped.class, new Scope() {
            @Override
            public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
                return new Provider<T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T get() {
                        Class<T> instanceClass = (Class<T>) key.getTypeLiteral().getType();
                        T instance = ResteasyProviderFactory.getContextData(instanceClass);

                        if (instance == null) {
                            instance = creator.get();
                            ResteasyProviderFactory.pushContext(instanceClass, instance);
                        }

                        return instance;
                    }

                    @Override
                    public String toString() {
                        return String.format("%s[%s]", creator, super.toString());
                    }
                };
            }
        });

        bind(HttpServletRequest.class).toProvider(new ResteasyContextProvider<>(HttpServletRequest.class)).in(RequestScoped.class);
        bind(HttpServletResponse.class).toProvider(new ResteasyContextProvider<>(HttpServletResponse.class)).in(RequestScoped.class);
        bind(Request.class).toProvider(new ResteasyContextProvider<>(Request.class)).in(RequestScoped.class);
        bind(HttpHeaders.class).toProvider(new ResteasyContextProvider<>(HttpHeaders.class)).in(RequestScoped.class);
        bind(UriInfo.class).toProvider(new ResteasyContextProvider<>(UriInfo.class)).in(RequestScoped.class);
        bind(SecurityContext.class).toProvider(new ResteasyContextProvider<>(SecurityContext.class)).in(RequestScoped.class);
    }

    private static class ResteasyContextProvider<T> implements Provider<T> {

        private final Class<T> instanceClass;

        public ResteasyContextProvider(Class<T> instanceClass) {
            this.instanceClass = instanceClass;
        }

        @Override
        public T get() {
            return ResteasyProviderFactory.getContextData(instanceClass);
        }
    }
}
