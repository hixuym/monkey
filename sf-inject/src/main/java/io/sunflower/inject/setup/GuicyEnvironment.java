package io.sunflower.inject.setup;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.sunflower.inject.Injectors;
import io.sunflower.inject.LoggerProvider;
import io.sunflower.inject.lifecycle.LifecycleSupport;
import io.sunflower.inject.scheduler.SchedulerSupport;

public class GuicyEnvironment {

    static Logger LOG = LoggerFactory.getLogger(GuicyEnvironment.class);

    private final List<Module> moduleLoaded = Lists.newArrayList();

    private final List<String> scanPkgs = Lists.newArrayList();

    private Injector injector;

    private AtomicBoolean commited = new AtomicBoolean(false);

    public GuicyEnvironment() {
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
            throw new RuntimeException("Guicy Environment Commited.");
        }

        Stopwatch sw = Stopwatch.createStarted();

        this.injector = Injectors.createInjector(Stage.PRODUCTION, moduleLoaded);

        sw.stop();

        if (LOG.isDebugEnabled()) {
            LOG.debug("注入器创建耗时：{}", sw);
        }

        commited.set(true);
    }

    public Injector injector() {
        if (!commited.get()) {
            throw new RuntimeException("Guicy Environment Uncommited.");
        }

        return injector;
    }
}
