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

package io.sunflower.resteasy.internal;

import com.google.inject.Provider;
import org.jboss.resteasy.spi.*;

/**
 * @author michael
 */
public class GuiceResourceFactory implements ResourceFactory {

    private final Provider provider;
    private final Class<?> scannableClass;
    private PropertyInjector propertyInjector;

    public GuiceResourceFactory(final Provider provider, final Class<?> scannableClass) {
        this.provider = provider;
        this.scannableClass = scannableClass;
    }

    @Override
    public Class<?> getScannableClass() {
        return scannableClass;
    }

    @Override
    public void registered(ResteasyProviderFactory factory) {
        propertyInjector = factory.getInjectorFactory().createPropertyInjector(scannableClass, factory);
    }

    @Override
    public Object createResource(final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory factory) {
        final Object resource = provider.get();
        propertyInjector.inject(request, response, resource);
        return resource;
    }

    @Override
    public void requestFinished(final HttpRequest request, final HttpResponse response, final Object resource) {
    }

    @Override
    public void unregistered() {
    }
}
