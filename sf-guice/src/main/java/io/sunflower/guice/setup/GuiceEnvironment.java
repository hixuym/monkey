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

package io.sunflower.guice.setup;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.sunflower.guice.InjectorBuilder;
import io.sunflower.guice.InjectorProcessor;
import io.sunflower.guice.LoggerProvider;
import io.sunflower.guice.ModulesEx;
import io.sunflower.guice.advise.AdvisableAnnotatedMethodScanner;
import io.sunflower.guice.event.guava.GuavaApplicationEventModule;
import io.sunflower.guice.lifecycle.LifecycleSupport;
import io.sunflower.guice.metrics.MetricsModule;
import io.sunflower.guice.scheduler.SchedulerSupport;
import io.sunflower.guice.visitors.BindingTracingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michael
 */
public class GuiceEnvironment {

  private static Logger LOG = LoggerFactory.getLogger(GuiceEnvironment.class);

  private final List<Module> moduleLoaded = Lists.newArrayList();
  private final List<Module> overrideModules = Lists.newArrayList();

  private Injector injector;

  private boolean setuped = false;

  private boolean scheduleEnabled = false;
  private boolean eventEnabled = false;
  private boolean adviseEnabled = false;
  private boolean metricsEnabled = false;
  private boolean lifecycleEnabled = false;

  private List<InjectorProcessor> injectorProcessors = Lists.newArrayList();

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

  public void override(Module... modules) {
    checkNotSetuped();
    this.overrideModules.addAll(Arrays.asList(modules));
  }

  public void addInjectorProcessor(InjectorProcessor processor) {
    this.injectorProcessors.add(processor);
  }

  public void setup() {

    checkNotSetuped();

    Stopwatch sw = Stopwatch.createStarted();

    if (scheduleEnabled) {
      register(SchedulerSupport.getModule());
    }

    if (eventEnabled) {
      register(new GuavaApplicationEventModule());
    }

    if (adviseEnabled) {
      register(AdvisableAnnotatedMethodScanner.asModule());
    }

    if (lifecycleEnabled) {
      register(LifecycleSupport.getModule());
    }

    if (metricsEnabled) {
      register(new MetricsModule());
    }

    InjectorBuilder builder = InjectorBuilder.fromModules(moduleLoaded);

    if (!overrideModules.isEmpty()) {
      builder.overrideWith(overrideModules);
    }

    builder.warnOfStaticInjections()
        .forEachElement(new BindingTracingVisitor(), LOG::debug);

    this.injector = builder.createInjector(Stage.PRODUCTION);

    injectorProcessors.forEach(it -> it.process(injector));

    sw.stop();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Guice Injector create time: {}", sw);
    }

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
