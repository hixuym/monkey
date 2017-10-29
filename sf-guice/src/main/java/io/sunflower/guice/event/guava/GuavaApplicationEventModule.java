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

package io.sunflower.guice.event.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import io.sunflower.guice.event.*;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author michael
 */
public final class GuavaApplicationEventModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationEventModule());
        bind(ApplicationEventDispatcher.class).to(GuavaApplicationEventDispatcher.class)
                .asEagerSingleton();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "GuavaApplicationEventModule[]";
    }

    private static final class GuavaApplicationEventDispatcher implements ApplicationEventDispatcher {

        private final EventBus eventBus;
        private final Method eventListenerMethod;

        @Inject
        public GuavaApplicationEventDispatcher(EventBus eventBus) {
            this.eventBus = eventBus;
            try {
                this.eventListenerMethod = ApplicationEventListener.class
                        .getDeclaredMethod("onEvent", ApplicationEvent.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache ApplicationEventListener method", e);
            }
        }

        public ApplicationEventRegistration registerListener(Object instance, Method method,
                                                             Class<? extends ApplicationEvent> eventType) {
            GuavaSubscriberProxy proxy = new GuavaSubscriberProxy(instance, method, eventType);
            eventBus.register(proxy);
            return new GuavaEventRegistration(eventBus, proxy);
        }

        public <T extends ApplicationEvent> ApplicationEventRegistration registerListener(
                Class<T> eventType, ApplicationEventListener<T> eventListener) {
            GuavaSubscriberProxy proxy = new GuavaSubscriberProxy(eventListener, eventListenerMethod,
                    eventType);
            eventBus.register(proxy);
            return new GuavaEventRegistration(eventBus, proxy);
        }

        public ApplicationEventRegistration registerListener(
                ApplicationEventListener<? extends ApplicationEvent> eventListener) {
            Type[] genericInterfaces = eventListener.getClass().getGenericInterfaces();
            for (Type type : genericInterfaces) {
                if (ApplicationEventListener.class.isAssignableFrom(TypeToken.of(type).getRawType())) {
                    ParameterizedType ptype = (ParameterizedType) type;
                    Class<?> rawType = TypeToken.of(ptype.getActualTypeArguments()[0]).getRawType();
                    GuavaSubscriberProxy proxy = new GuavaSubscriberProxy(eventListener, eventListenerMethod,
                            rawType);
                    eventBus.register(proxy);
                    return new GuavaEventRegistration(eventBus, proxy);
                }
            }
            //no-op. Could not find anything to register.
            return () -> {
            };
        }

        private static class GuavaSubscriberProxy {

            private final Object handlerInstance;
            private final Method handlerMethod;
            private final Class<?> acceptedType;

            public GuavaSubscriberProxy(Object handlerInstance, Method handlerMethod,
                                        Class<?> acceptedType) {
                this.handlerInstance = handlerInstance;
                this.handlerMethod = handlerMethod;
                this.acceptedType = acceptedType;
            }

            @Subscribe
            public void invokeEventHandler(ApplicationEvent event)
                    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (acceptedType.isAssignableFrom(event.getClass())) {
                    if (!handlerMethod.isAccessible()) {
                        handlerMethod.setAccessible(true);
                    }
                    handlerMethod.invoke(handlerInstance, event);
                }
            }
        }

        private static class GuavaEventRegistration implements ApplicationEventRegistration {

            private final EventBus eventBus;
            private final GuavaSubscriberProxy subscriber;

            public GuavaEventRegistration(EventBus eventBus, GuavaSubscriberProxy subscriber) {
                this.eventBus = eventBus;
                this.subscriber = subscriber;
            }

            public void unregister() {
                this.eventBus.unregister(subscriber);
            }
        }

        @Override
        public void fire(ApplicationEvent event) {
            this.eventBus.post(event);
        }
    }
}
