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

package io.sunflower.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.spi.Element;
import io.sunflower.guice.visitors.BindingTracingVisitor;
import io.sunflower.guice.visitors.KeyTracingVisitor;
import io.sunflower.guice.visitors.ModuleSourceTracingVisitor;
import io.sunflower.guice.visitors.WarnOfToInstanceInjectionVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.Consumer;

public class InjectorBuilderTest {

    @Test
    public void testLifecycleInjectorEvents() {
        InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                    }
                })
                .createInjector();
    }

    @Before
    public void printTestHeader() {
        System.out.println("\n=======================================================");
        System.out.println("  Running Test : " + name.getMethodName());
        System.out.println("=======================================================\n");
    }

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBindingTracing() {
        InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(String.class).toInstance("Hello world");
                    }
                })
                .forEachElement(new BindingTracingVisitor())
                .createInjector();
    }

    @Test
    public void testForEachBinding() {
        Consumer<String> consumer = Mockito.mock(Consumer.class);
        InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(String.class).toInstance("Hello world");
                    }
                })
                .forEachElement(new WarnOfToInstanceInjectionVisitor(), consumer)
                .createInjector();

        Mockito.verify(consumer, Mockito.times(1)).accept(Mockito.anyString());
    }

    @Test
    public void testKeyTracing() {
        Injector li = InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(String.class).toInstance("Hello world");
                    }
                })
                .forEachElement(new KeyTracingVisitor())
                .createInjector();
    }

    @Test
    public void testWarnOnStaticInjection() {
        List<Element> elements = InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        this.requestStaticInjection(String.class);
                    }
                })
                .warnOfStaticInjections()
                .getElements();

        Assert.assertEquals(1, elements.size());
    }

    @Test
    public void testStripStaticInjection() {
        List<Element> elements = InjectorBuilder
                .fromModule(new AbstractModule() {
                    @Override
                    protected void configure() {
                        this.requestStaticInjection(String.class);
                    }
                })
                .stripStaticInjections()
                .warnOfStaticInjections()
                .getElements();

        Assert.assertEquals(0, elements.size());
    }

    public static class ModuleA extends AbstractModule {

        @Override
        protected void configure() {
            install(new ModuleB());
            install(new ModuleC());
        }
    }

    public static class ModuleB extends AbstractModule {

        @Override
        protected void configure() {
            install(new ModuleC());
        }
    }

    public static class ModuleC extends AbstractModule {

        @Override
        protected void configure() {
            bind(String.class).toInstance("Hello world");
        }

        @Override
        public int hashCode() {
            return ModuleC.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass().equals(getClass());
        }
    }

    @Test
    public void testTraceModules() {
        InjectorBuilder
                .fromModule(new ModuleA())
                .forEachElement(new ModuleSourceTracingVisitor())
                .createInjector();
    }
}
