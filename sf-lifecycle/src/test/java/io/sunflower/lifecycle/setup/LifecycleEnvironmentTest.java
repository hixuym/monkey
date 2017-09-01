package io.sunflower.lifecycle.setup;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class LifecycleEnvironmentTest {

    private final LifecycleEnvironment environment = new LifecycleEnvironment();

    @Test
    public void scheduledExecutorServiceBuildsDaemonThreads() throws ExecutionException, InterruptedException {
        final ScheduledExecutorService executorService = environment.scheduledExecutorService("daemon-%d", true).build();
        final Future<Boolean> isDaemon = executorService.submit(() -> Thread.currentThread().isDaemon());

        assertThat(isDaemon.get()).isTrue();
    }

    @Test
    public void scheduledExecutorServiceBuildsUserThreadsByDefault() throws ExecutionException, InterruptedException {
        final ScheduledExecutorService executorService = environment.scheduledExecutorService("user-%d").build();
        final Future<Boolean> isDaemon = executorService.submit(() -> Thread.currentThread().isDaemon());

        assertThat(isDaemon.get()).isFalse();
    }

    @Test
    public void scheduledExecutorServiceThreadFactory() throws ExecutionException, InterruptedException {
        final String expectedName = "Sunflower ThreadFactory Test";
        final String expectedNamePattern = expectedName + "-%d";

        final ThreadFactory tfactory = (new ThreadFactoryBuilder())
            .setDaemon(false)
            .setNameFormat(expectedNamePattern)
            .build();

        final ScheduledExecutorService executorService = environment.scheduledExecutorService("Sunflower Service", tfactory).build();
        final Future<Boolean> isFactoryInUse = executorService.submit(() -> Thread.currentThread().getName().startsWith(expectedName));

        assertThat(isFactoryInUse.get()).isTrue();
    }

    @Test
    public void executorServiceThreadFactory() throws ExecutionException, InterruptedException {
        final String expectedName = "Sunflower ThreadFactory Test";
        final String expectedNamePattern = expectedName + "-%d";

        final ThreadFactory tfactory = (new ThreadFactoryBuilder())
            .setDaemon(false)
            .setNameFormat(expectedNamePattern)
            .build();

        final ExecutorService executorService = environment.executorService("Sunflower Service", tfactory).build();
        final Future<Boolean> isFactoryInUse = executorService.submit(() -> Thread.currentThread().getName().startsWith(expectedName));

        assertThat(isFactoryInUse.get()).isTrue();
    }
}
