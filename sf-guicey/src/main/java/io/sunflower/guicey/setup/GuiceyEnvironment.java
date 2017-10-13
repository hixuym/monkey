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

package io.sunflower.guicey.setup;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.sunflower.guicey.InjectorBuilder;
import io.sunflower.guicey.LoggerProvider;
import io.sunflower.guicey.advise.AdvisableAnnotatedMethodScanner;
import io.sunflower.guicey.event.guava.GuavaApplicationEventModule;
import io.sunflower.guicey.lifecycle.LifecycleSupport;
import io.sunflower.guicey.metrics.MetricsModule;
import io.sunflower.guicey.scheduler.SchedulerSupport;
import io.sunflower.guicey.visitors.BindingTracingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceyEnvironment {

  private static Logger LOG = LoggerFactory.getLogger(GuiceyEnvironment.class);

  private final List<Module> moduleLoaded = Lists.newArrayList();
  private final List<Module> overrideModules = Lists.newArrayList();
  private final List<Module> combineWithModules = Lists.newArrayList();

  private Injector injector;

  private boolean commited = false;

  public GuiceyEnvironment() {
    System.setProperty("file.encoding", "utf-8");
    install(LifecycleSupport.getModule(),
        SchedulerSupport.getModule(),
        new GuavaApplicationEventModule(),
        new MetricsModule(),
        AdvisableAnnotatedMethodScanner.asModule(),
        new AbstractModule() {
          @Override
          protected void configure() {
            binder().disableCircularProxies();
            bind(Logger.class).toProvider(LoggerProvider.class);
          }
        });
  }

  public void install(Module... modules) {
    checkNotCommited();
    this.moduleLoaded.addAll(Arrays.asList(modules));
  }

  public void override(Module... modules) {
    checkNotCommited();
    this.overrideModules.addAll(Arrays.asList(modules));
  }

  public void combineWith(Module... modules) {
    checkNotCommited();
    this.combineWithModules.addAll(Arrays.asList(modules));
  }

  public void commit() {

    checkNotCommited();

    Stopwatch sw = Stopwatch.createStarted();

    InjectorBuilder builder = InjectorBuilder.fromModules(moduleLoaded);

    if (!overrideModules.isEmpty()) {
      builder.overrideWith(overrideModules);
    }

    if (!combineWithModules.isEmpty()) {
      builder.combineWith(combineWithModules.toArray(new Module[]{}));
    }

    builder.warnOfStaticInjections()
        .forEachElement(new BindingTracingVisitor(), LOG::debug);

    this.injector = builder.createInjector();

    sw.stop();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Guice Injector create time: {}", sw);
    }

    this.commited = true;
  }

  public Injector getInjector() {
    if (this.injector == null) {
      throw new IllegalStateException("commit the guicey environment first.");
    }
    return injector;
  }

  private void checkNotCommited() {
    if (commited) {
      throw new IllegalStateException("guicey already commited.");
    }
  }
}
