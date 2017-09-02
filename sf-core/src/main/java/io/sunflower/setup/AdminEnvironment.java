package io.sunflower.setup;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.lifecycle.setup.LifecycleEnvironment;
import io.sunflower.undertow.handler.GarbageCollectionTask;
import io.sunflower.undertow.handler.LogConfigurationTask;
import io.sunflower.undertow.handler.Task;
import io.sunflower.undertow.handler.TaskHandler;
import io.sunflower.undertow.setup.UndertowEnvironment;
import io.undertow.server.handlers.PathHandler;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

/**
 * Created by michael on 17/9/1.
 */
public class AdminEnvironment extends UndertowEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEnvironment.class);

    private final HealthCheckRegistry healthChecks;

    private final TaskHandler tasks;

    public AdminEnvironment(PathHandler handlerContext,
                            HealthCheckRegistry healthChecks,
                            MetricRegistry metricRegistry,
                            LifecycleEnvironment lifecycle) {

        super(handlerContext);

        this.healthChecks = healthChecks;
        this.healthChecks.register("deadlocks", new ThreadDeadlockHealthCheck());
        this.tasks = new TaskHandler(metricRegistry);

        tasks.add(new GarbageCollectionTask());
        tasks.add(new LogConfigurationTask());

        addUndertowHandler(TaskHandler.MAPPING, tasks);

        lifecycle.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                logTasks();
                logHealthChecks();
            }
        });
    }

    /**
     * Adds the given task to the set of tasks exposed via the admin interface.
     *
     * @param task a task
     */
    public void addTask(Task task) {
        tasks.add(requireNonNull(task));
    }

    private void logTasks() {
        final StringBuilder stringBuilder = new StringBuilder(1024).append(String.format("%n%n"));

        for (Task task : tasks.getTasks()) {
            final String taskClassName = firstNonNull(task.getClass().getCanonicalName(), task.getClass().getName());
            stringBuilder.append(String.format("    %-7s /tasks/%s (%s)%n",
                "POST",
                task.getName(),
                taskClassName));
        }

        LOGGER.info("tasks = {}", stringBuilder.toString());
    }

    private void logHealthChecks() {
        if (healthChecks.getNames().size() <= 1) {
            LOGGER.warn(String.format(
                "%n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                    "!    THIS APPLICATION HAS NO HEALTHCHECKS. THIS MEANS YOU WILL NEVER KNOW      !%n" +
                    "!     IF IT DIES IN PRODUCTION, WHICH MEANS YOU WILL NEVER KNOW IF YOU'RE      !%n" +
                    "!    LETTING YOUR USERS DOWN. YOU SHOULD ADD A HEALTHCHECK FOR EACH OF YOUR    !%n" +
                    "!         APPLICATION'S DEPENDENCIES WHICH FULLY (BUT LIGHTLY) TESTS IT.       !%n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!%n" +
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            ));
        }
        LOGGER.debug("health checks = {}", healthChecks.getNames());
    }
}
