package io.sunflower.guicey.setup;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.sunflower.guicey.LoggerProvider;
import io.sunflower.guicey.lifecycle.LifecycleSupport;
import io.sunflower.guicey.scheduler.SchedulerSupport;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceyEnvironment {

  private static Logger LOG = LoggerFactory.getLogger(GuiceyEnvironment.class);

  private final List<Module> moduleLoaded = Lists.newArrayList();

  private final List<String> scanPkgs = Lists.newArrayList();

  private Injector injector;

  private AtomicBoolean commited = new AtomicBoolean(false);

  public GuiceyEnvironment() {
    System.setProperty("file.encoding", "utf-8");
    addModule(LifecycleSupport.getModule(), SchedulerSupport.getModule(), new AbstractModule() {
      @Override
      protected void configure() {
        bind(Logger.class).toProvider(LoggerProvider.class);
      }
    });
  }

  public void addModule(Module... modules) {
    this.moduleLoaded.addAll(Arrays.asList(modules));
  }

  public void scanPkgs(String... scanPkgs) {
    this.scanPkgs.addAll(Arrays.asList(scanPkgs));
  }

  public void commit() {
    if (commited.get()) {
      throw new RuntimeException("guicey environment have aready commited.");
    }

    Stopwatch sw = Stopwatch.createStarted();

    this.injector = Guice.createInjector(Stage.PRODUCTION, moduleLoaded);

    sw.stop();

    if (LOG.isDebugEnabled()) {
      LOG.debug("injector create time: {}", sw);
    }

    commited.set(true);
  }

  public Injector injector() {
    if (!commited.get()) {
      throw new RuntimeException("guicey environment haven't commited.");
    }

    return injector;
  }
}
