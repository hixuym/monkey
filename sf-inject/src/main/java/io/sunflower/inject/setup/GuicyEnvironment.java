package io.sunflower.inject.setup;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.sunflower.inject.Injectors;

public class GuicyEnvironment {

    private final List<Module> moduleLoaded = Lists.newArrayList();

    private final List<String> scanPkgs = Lists.newArrayList();

    private Injector injector;

    private AtomicBoolean commited = new AtomicBoolean(false);

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

        this.injector = Injectors.createInjector(Stage.PRODUCTION, moduleLoaded);

        commited.set(true);
    }

    public Injector injector() {
        if (!commited.get()) {
            throw new RuntimeException("Guicy Environment Uncommited.");
        }

        return injector;
    }
}
