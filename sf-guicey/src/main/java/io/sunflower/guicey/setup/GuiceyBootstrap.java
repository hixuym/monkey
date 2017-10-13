package io.sunflower.guicey.setup;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.sunflower.guicey.InjectorBuilder;
import io.sunflower.guicey.LoggerProvider;
import io.sunflower.guicey.event.guava.GuavaApplicationEventModule;
import io.sunflower.guicey.lifecycle.LifecycleManager;
import io.sunflower.guicey.lifecycle.LifecycleSupport;
import io.sunflower.guicey.scheduler.SchedulerSupport;
import io.sunflower.guicey.visitors.BindingTracingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceyBootstrap {

  private static Logger LOG = LoggerFactory.getLogger(GuiceyBootstrap.class);

  private final List<Module> moduleLoaded = Lists.newArrayList();
  private final List<Module> overrideModules = Lists.newArrayList();
  private final List<Module> combineWithModules = Lists.newArrayList();

  private Injector injector;

  private AtomicBoolean commited = new AtomicBoolean(false);

  public GuiceyBootstrap() {
    System.setProperty("file.encoding", "utf-8");
    addModule(LifecycleSupport.getModule(),
        SchedulerSupport.getModule(),
        new GuavaApplicationEventModule(),
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(Logger.class).toProvider(LoggerProvider.class);
          }
        });
  }

  public void addModule(Module... modules) {
    this.moduleLoaded.addAll(Arrays.asList(modules));
  }

  public void overrideModule(Module... modules) {
    this.overrideModules.addAll(Arrays.asList(modules));
  }

  public void combineWith(Module... modules) {
    this.combineWithModules.addAll(Arrays.asList(modules));
  }

  public void commit() {
    if (commited.get()) {
      throw new RuntimeException("guicey environment have aready commited.");
    }

    Stopwatch sw = Stopwatch.createStarted();

    InjectorBuilder builder = InjectorBuilder.fromModules(moduleLoaded);

    if (!overrideModules.isEmpty()) {
      builder.overrideWith(overrideModules);
    }

    if (!combineWithModules.isEmpty()) {
      builder.combineWith(combineWithModules.toArray(new Module[]{}));
    }

    builder.warnOfStaticInjections()
        .forEachElement(new BindingTracingVisitor());

    this.injector = builder.createInjector(Stage.PRODUCTION);

    sw.stop();

    if (LOG.isDebugEnabled()) {
      LOG.debug("getInjector create time: {}", sw);
    }

    commited.set(true);
  }

  public void dispose() {
    if (this.injector != null) {
      injector.getInstance(LifecycleManager.class).stop();
      injector = null;
    }
  }

  public Injector getInjector() {
    if (!commited.get()) {
      throw new RuntimeException("guicey environment haven't commited.");
    }

    return injector;
  }
}
