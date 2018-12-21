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

package io.sunflower.setup;

import com.google.common.base.Stopwatch;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.sunflower.Mode;
import io.sunflower.inject.*;
import io.sunflower.inject.advise.AdvisableAnnotatedMethodScanner;
import io.sunflower.inject.event.guava.GuavaApplicationEventModule;
import io.sunflower.inject.lifecycle.LifecycleSupport;
import io.sunflower.inject.metrics.MetricsModule;
import io.sunflower.inject.scheduler.SchedulerSupport;
import io.sunflower.inject.visitors.BindingTracingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author michael
 */
public class GuiceEnvironment {

    private static Logger LOG = LoggerFactory.getLogger(GuiceEnvironment.class);

    private final List<Module> moduleLoaded = newArrayList();
    private final List<Module> overrideModules = newArrayList();
    private final List<ModulesProcessor> modulesProcessors = newArrayList();

    private volatile Injector injector;

    private boolean setuped = false;

    private boolean scheduleEnabled = false;
    private boolean eventEnabled = false;
    private boolean adviseEnabled = false;
    private boolean metricsEnabled = false;
    private boolean lifecycleEnabled = false;

    public GuiceEnvironment() {
        System.setProperty("file.encoding", "utf-8");
        register(new AbstractModule() {
            @Override
            protected void configure() {
                binder().disableCircularProxies();
                bind(Logger.class).toProvider(LoggerProvider.class);
            }
        });
    }

    public void enableLifecycle() {
        this.lifecycleEnabled = true;
    }

    public void enableMetrics() {
        this.metricsEnabled = true;
    }

    public void enableEvent() {
        this.eventEnabled = true;
    }

    public void enableAdvise() {
        this.adviseEnabled = true;
    }

    public void enableScheduler() {
        this.scheduleEnabled = true;
    }

    public void register(Module... modules) {
        checkNotSetuped();
        this.moduleLoaded.addAll(Arrays.asList(modules));
    }

    public void register(Module module) {
        checkNotSetuped();
        this.moduleLoaded.add(module);
    }

    public <T> void register(final T object) {
        checkNotSetuped();
        if (object instanceof Module) {
            register((Module) object);
        } else {
            register(ModulesEx.fromInstance(object));
        }
    }

    public <T> void register(final Class<T> tClass) {
        checkNotSetuped();
        register(ModulesEx.fromEagerSingleton(tClass));
    }

    public void overrideWith(Module... modules) {
        checkNotSetuped();
        this.overrideModules.addAll(Arrays.asList(modules));
    }

    public void registerModuleProcessor(ModulesProcessor modulesProcessor) {
        this.modulesProcessors.add(modulesProcessor);
    }

    public void setup() {
        setup(Mode.prod);
    }

    public void setup(Mode mode) {

        checkNotSetuped();

        Stopwatch sw = Stopwatch.createStarted();

        if (scheduleEnabled) {
            register(SchedulerSupport.asModule());
        }

        if (eventEnabled) {
            register(new GuavaApplicationEventModule());
        }

        if (adviseEnabled) {
            register(AdvisableAnnotatedMethodScanner.asModule());
        }

        if (lifecycleEnabled) {
            register(LifecycleSupport.asModule());
        }

        if (metricsEnabled) {
            register(new MetricsModule());
        }

        modulesProcessors.forEach(it -> it.process(moduleLoaded));

        InjectorBuilder builder = InjectorBuilder.fromModules(moduleLoaded);

        if (!overrideModules.isEmpty()) {
            builder.overrideWith(overrideModules);
        }

        builder.warnOfStaticInjections()
                .forEachElement(new BindingTracingVisitor(), LOG::debug);

        this.injector = builder.createInjector(mode == Mode.prod ? Stage.PRODUCTION : Stage.DEVELOPMENT);

        sw.stop();

        if (LOG.isDebugEnabled()) {
            LOG.debug("guice environment setup time: {}", sw);
        }

        this.moduleLoaded.clear();
        this.overrideModules.clear();
        this.modulesProcessors.clear();
        this.setuped = true;
    }

    public Injector getInjector() {
        if (this.injector == null) {
            throw new IllegalStateException("setup the guice environment first.");
        }
        return injector;
    }

    private void checkNotSetuped() {
        if (setuped) {
            throw new IllegalStateException("guice already setuped.");
        }
    }
}
