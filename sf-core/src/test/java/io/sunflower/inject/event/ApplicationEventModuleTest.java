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

package io.sunflower.inject.event;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import io.sunflower.inject.InjectorBuilder;
import io.sunflower.inject.event.guava.GuavaApplicationEventModule;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ApplicationEventModuleTest {

    private Injector injector;

    @Before
    public void setup() {
        injector = InjectorBuilder.fromModules(new GuavaApplicationEventModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestAnnotatedListener.class).toInstance(new TestAnnotatedListener());
                bind(TestListenerInterface.class).toInstance(new TestListenerInterface());
            }
        }).createInjector();
    }

    @Test
    public void testProvidedComponentsPresent() {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        TestAnnotatedListener listener = injector.getInstance(TestAnnotatedListener.class);
        TestListenerInterface listenerInterface = injector.getInstance(TestListenerInterface.class);
        assertNotNull(dispatcher);
        assertNotNull(listener);
        assertNotNull(listenerInterface);
    }

    @Test
    public void testAnnotatedListener() throws Exception {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        TestAnnotatedListener listener = injector.getInstance(TestAnnotatedListener.class);
        assertEquals(0, listener.invocationCount.get());
        dispatcher.fire(new TestEvent());
        assertEquals(1, listener.invocationCount.get());
        dispatcher.fire(new NotTestEvent());
        assertEquals(1, listener.invocationCount.get());
    }

    @Test
    public void testManuallyRegisteredApplicationEventListeners() throws Exception {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        final AtomicInteger testEventCounter = new AtomicInteger();
        final AtomicInteger notTestEventCounter = new AtomicInteger();
        final AtomicInteger allEventCounter = new AtomicInteger();

        dispatcher.registerListener(TestEvent.class, event -> testEventCounter.incrementAndGet());
        dispatcher.registerListener(NotTestEvent.class, event -> notTestEventCounter.incrementAndGet());
        dispatcher.registerListener(ApplicationEvent.class, event -> allEventCounter.incrementAndGet());

        dispatcher.fire(new TestEvent());
        assertEquals(1, testEventCounter.get());
        assertEquals(0, notTestEventCounter.get());
        assertEquals(1, allEventCounter.get());
    }

    @Test
    public void testManuallyRegisteredApplicationEventListenersWithoutClassArgument()
            throws Exception {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        final AtomicInteger testEventCounter = new AtomicInteger();
        final AtomicInteger notTestEventCounter = new AtomicInteger();
        final AtomicInteger allEventCounter = new AtomicInteger();

        dispatcher.registerListener(TestEvent.class, event -> testEventCounter.incrementAndGet());
        dispatcher.registerListener(NotTestEvent.class, event -> notTestEventCounter.incrementAndGet());
        dispatcher.registerListener(TestEvent.class, event -> allEventCounter.incrementAndGet());

        dispatcher.fire(new TestEvent());
        assertEquals(1, testEventCounter.get());
        assertEquals(0, notTestEventCounter.get());
        assertEquals(1, allEventCounter.get());
    }

    @Test
    public void testInjectorDiscoveredApplicationEventListeners() throws Exception {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        TestListenerInterface listener = injector.getInstance(TestListenerInterface.class);
        assertEquals(0, listener.invocationCount.get());
        dispatcher.fire(new TestEvent());
        assertEquals(1, listener.invocationCount.get());
        dispatcher.fire(new NotTestEvent());
        assertEquals(1, listener.invocationCount.get());
    }

    @Test
    public void testUnregisterApplicationEventListener() throws Exception {
        ApplicationEventDispatcher dispatcher = injector.getInstance(ApplicationEventDispatcher.class);
        final AtomicInteger testEventCounter = new AtomicInteger();

        ApplicationEventRegistration registration = dispatcher
                .registerListener(TestEvent.class, event -> testEventCounter.incrementAndGet());

        dispatcher.fire(new TestEvent());
        assertEquals(1, testEventCounter.get());
        registration.unregister();
        assertEquals(1, testEventCounter.get());
    }

    @Test(expected = CreationException.class)
    public void testEventListenerWithInvalidArgumentsFailsFast() {
        injector = InjectorBuilder.fromModules(new GuavaApplicationEventModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestFailFastEventListener.class).toInstance(new TestFailFastEventListener());
            }
        }).createInjector();
    }

    private class TestAnnotatedListener {

        AtomicInteger invocationCount = new AtomicInteger();

        @EventListener
        public void doThing(TestEvent event) {
            invocationCount.incrementAndGet();
        }
    }

    private class TestFailFastEventListener {

        @EventListener
        public void doNothing(String invalidArgumentType) {
            fail("This should never be called");
        }
    }

    private class TestListenerInterface implements ApplicationEventListener<TestEvent> {

        AtomicInteger invocationCount = new AtomicInteger();

        @Override
        public void onEvent(TestEvent event) {
            invocationCount.incrementAndGet();
        }
    }

    private class TestEvent implements ApplicationEvent {

    }

    private class NotTestEvent implements ApplicationEvent {

    }

}