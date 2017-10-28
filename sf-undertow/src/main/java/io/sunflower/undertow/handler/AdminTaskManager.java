package io.sunflower.undertow.handler;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.MediaType;
import io.sunflower.guice.Injectors;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.setup.Environment;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * @author michael
 * @date 17/9/1
 */
public class AdminTaskManager implements HttpHandler {

    private static final long serialVersionUID = 7404713218661358124L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminTaskManager.class);
    private final ConcurrentMap<String, Task> tasks;

    private final ConcurrentMap<Task, TaskExecutor> taskExecutors;

    /**
     * Creates a new AdminTaskManager.
     */
    @Inject
    public AdminTaskManager(Environment environment) {
        this.tasks = new ConcurrentHashMap<>();
        this.taskExecutors = new ConcurrentHashMap<>();

        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {

                Injectors.instanceOf(environment.injector(), Task.class)
                        .forEach(AdminTaskManager.this::add);

                logTasks();
            }
        });
    }

    private void doGet(HttpServerExchange exchange) {
        if (Strings.isNullOrEmpty(exchange.getRelativePath())) {

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

            StringWriter stringWriter = new StringWriter();

            try (final PrintWriter output = new PrintWriter(stringWriter)) {
                getTasks().stream()
                        .map(Task::getName)
                        .sorted()
                        .forEach(output::println);

                output.flush();
            }

            exchange.getResponseSender().send(stringWriter.toString());

        } else if (tasks.containsKey(exchange.getRelativePath())) {
            exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
            exchange.endExchange();
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.endExchange();
        }
    }

    private void doPost(HttpServerExchange exchange) {
        final Task task = tasks.get(exchange.getRelativePath());
        if (task != null) {
            exchange.getResponseHeaders()
                    .put(Headers.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
            StringWriter stringWriter = new StringWriter();
            final PrintWriter output = new PrintWriter(stringWriter);
            try {
                final TaskExecutor taskExecutor = taskExecutors.get(task);
                taskExecutor.executeTask(getParams(exchange), output);
                exchange.getResponseSender().send(stringWriter.toString());
            } catch (Exception e) {
                LOGGER.error("Error running {}", task.getName(), e);
                exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                output.println();
                output.println(e.getMessage());
                e.printStackTrace(output);
                exchange.endExchange();
            } finally {
                output.close();
            }
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.endExchange();
        }
    }

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString method = exchange.getRequestMethod();

        if (GET.equalsIgnoreCase(method.toString())) {
            doGet(exchange);
        }

        if (POST.equalsIgnoreCase(method.toString())) {
            doPost(exchange);
        }
    }

    private static ImmutableMultimap<String, String> getParams(HttpServerExchange exchange) {
        final ImmutableMultimap.Builder<String, String> results = ImmutableMultimap.builder();
        final Map<String, Deque<String>> params = exchange.getQueryParameters();

        for (Map.Entry<String, Deque<String>> e : params.entrySet()) {
            final String name = e.getKey();
            final Deque<String> values = e.getValue();
            results.putAll(name, values.toArray(new String[]{}));
        }
        return results.build();
    }

    public void add(Task task) {
        tasks.put('/' + task.getName(), task);

        TaskExecutor taskExecutor = new TaskExecutor(task);
        taskExecutors.put(task, taskExecutor);
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    private static class TaskExecutor {

        private final Task task;

        private TaskExecutor(Task task) {
            this.task = task;
        }

        public void executeTask(ImmutableMultimap<String, String> params, PrintWriter output)
                throws Exception {
            task.execute(params, output);
        }
    }

    private void logTasks() {
        final StringBuilder stringBuilder = new StringBuilder(1024).append(String.format("%n%n"));

        for (Task task : getTasks()) {
            final String taskClassName = firstNonNull(task.getClass().getCanonicalName(),
                    task.getClass().getName());
            stringBuilder.append(String.format("    %-7s /tasks/%s (%s)%n",
                    "POST",
                    task.getName(),
                    taskClassName));
        }

        LOGGER.info("tasks = {}", stringBuilder.toString());
    }
}
