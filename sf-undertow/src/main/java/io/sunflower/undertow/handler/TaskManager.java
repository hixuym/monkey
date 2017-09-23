package io.sunflower.undertow.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

/**
 * Created by michael on 17/9/1.
 */
public class TaskManager implements HttpHandler {

    private static final long serialVersionUID = 7404713218661358124L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);
    private final ConcurrentMap<String, Task> tasks;
    private final ConcurrentMap<Task, TaskExecutor> taskExecutors;

    public final static String MAPPING = "tasks";

    public static HttpHandler createHandler(TaskManager manager) {

        HttpHandler h = manager;

        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset("utf-8");
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);

        // then requests MUST be blocking for IO to function
        return new BlockingHandler(h);
    }

    /**
     * Creates a new TaskManager.
     */
    public TaskManager() {
        this.tasks = new ConcurrentHashMap<>();
        this.taskExecutors = new ConcurrentHashMap<>();

        add(new GarbageCollectionTask());
        add(new LogConfigurationTask());
    }

    private void doGet(HttpServerExchange exchange) {
        if (Strings.isNullOrEmpty(exchange.getRelativePath())) {

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

            try (final PrintWriter output = new PrintWriter(exchange.getOutputStream())) {
                getTasks().stream()
                    .map(Task::getName)
                    .sorted()
                    .forEach(output::println);

                output.flush();
            }

        } else if (tasks.containsKey(exchange.getRelativePath())) {
            exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }

        exchange.endExchange();
    }

    private void doPost(HttpServerExchange exchange) {
        final Task task = tasks.get(exchange.getRelativePath());
        if (task != null) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
            final PrintWriter output = new PrintWriter(exchange.getOutputStream());
            try {
                final TaskExecutor taskExecutor = taskExecutors.get(task);
                taskExecutor.executeTask(getParams(exchange), getBody(exchange), output);
                exchange.endExchange();
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

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        HttpString method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method.toString())) {
            doGet(exchange);
        }

        if ("POST".equalsIgnoreCase(method.toString())) {
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

    private String getBody(HttpServerExchange exchange) throws IOException {
        return CharStreams.toString(new InputStreamReader(exchange.getInputStream(), Charsets.UTF_8));
    }

    public void add(Task task) {
        tasks.put('/' + task.getName(), task);

        taskExecutors.put(task, new TaskExecutor(task));
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    private static class TaskExecutor {
        private final Task task;

        private TaskExecutor(Task task) {
            this.task = task;
        }

        public void executeTask(ImmutableMultimap<String, String> params, String body, PrintWriter output) throws Exception {
            if (task instanceof PostBodyTask) {
                PostBodyTask postBodyTask = (PostBodyTask) task;
                postBodyTask.execute(params, body, output);
            } else {
                task.execute(params, output);
            }
        }
    }
}
