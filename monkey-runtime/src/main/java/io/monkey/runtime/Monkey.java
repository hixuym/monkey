package io.monkey.runtime;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.env.Environment;
import io.micronaut.context.env.PropertySource;
import io.micronaut.core.cli.CommandLine;
import io.micronaut.core.naming.Described;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.context.env.CommandLinePropertySource;
import io.micronaut.runtime.exceptions.ApplicationStartupException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.monkey.context.MonkeyApplicationContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Michael
 * Created at: 2019/2/17 18:27
 */
public class Monkey extends MonkeyApplicationContextBuilder implements ApplicationContextBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(Monkey.class);

    private String[] args = new String[0];
    private Map<Class<? extends Throwable>, Function<Throwable, Integer>> exitHandlers = new LinkedHashMap<>();

    /**
     * The default constructor.
     */
    protected Monkey() {
    }

    /**
     * @return Run this {@link Monkey}
     */
    @Override
    public ApplicationContext start() {
        CommandLine commandLine = CommandLine.parse(args);
        propertySources(new CommandLinePropertySource(commandLine));
        ApplicationContext applicationContext = super.build();

        try {
            long start = System.currentTimeMillis();
            applicationContext.start();

            Optional<EmbeddedApplication> embeddedContainerBean = applicationContext.findBean(EmbeddedApplication.class);

            embeddedContainerBean.ifPresent((embeddedApplication -> {
                try {
                    embeddedApplication.start();

                    boolean keepAlive = false;
                    if (embeddedApplication instanceof Described) {
                        if (LOG.isInfoEnabled()) {
                            long end = System.currentTimeMillis();
                            long took = end - start;
                            String desc = ((Described) embeddedApplication).getDescription();
                            LOG.info("Startup completed in {}ms. Server Running: {}", took, desc);
                        }
                        keepAlive = embeddedApplication.isServer();
                    } else {
                        if (embeddedApplication instanceof EmbeddedServer) {

                            if (LOG.isInfoEnabled()) {
                                long end = System.currentTimeMillis();
                                long took = end - start;
                                URL url = ((EmbeddedServer) embeddedApplication).getURL();
                                LOG.info("Startup completed in {}ms. Server Running: {}", took, url);
                            }
                        } else {
                            if (LOG.isInfoEnabled()) {
                                long end = System.currentTimeMillis();
                                long took = end - start;
                                LOG.info("Startup completed in {}ms.", took);
                            }
                            keepAlive = embeddedApplication.isServer();
                        }
                    }

                    Thread mainThread = Thread.currentThread();
                    boolean finalKeepAlive = keepAlive;
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Embedded Application shutting down");
                        }
                        embeddedApplication.stop();
                        if (finalKeepAlive) {
                            mainThread.interrupt();
                        }
                    }));

                    if (keepAlive) {
                        try {
                            while (embeddedApplication.isRunning()) {
                                Thread.sleep(1000);
                            }
                            if (LOG.isInfoEnabled()) {
                                LOG.info("Embedded Application shutting down");
                            }
                            if (embeddedApplication.isForceExit()) {
                                System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }

                } catch (Throwable e) {
                    handleStartupException(applicationContext.getEnvironment(), e);
                }
            }));

            if (LOG.isInfoEnabled() && !embeddedContainerBean.isPresent()) {
                LOG.info("No embedded container found. Running as CLI application");
            }
            return applicationContext;
        } catch (Throwable e) {
            handleStartupException(applicationContext.getEnvironment(), e);
            return null;
        }
    }

    @Override
    public Monkey include(@Nullable String... configurations) {
        return (Monkey) super.include(configurations);
    }

    @Override
    public Monkey exclude(@Nullable String... configurations) {
        return (Monkey) super.exclude(configurations);
    }

    /**
     * Add classes to be included in the initialization of the application.
     *
     * @param classes The application
     * @return The classes
     */
    public Monkey classes(@Nullable Class... classes) {
        if (classes != null) {
            for (Class aClass : classes) {
                packages(aClass.getPackage().getName());
            }
        }
        return this;
    }

    @Override
    public Monkey properties(@Nullable Map<String, Object> properties) {
        return (Monkey) super.properties(properties);
    }

    @Override
    public Monkey singletons(Object... beans) {
        return (Monkey) super.singletons(beans);
    }

    @Override
    public Monkey propertySources(@Nullable PropertySource... propertySources) {
        return (Monkey) super.propertySources(propertySources);
    }

    @Override
    public Monkey mainClass(Class mainClass) {
        return (Monkey) super.mainClass(mainClass);
    }

    @Override
    public Monkey classLoader(ClassLoader classLoader) {
        return (Monkey) super.classLoader(classLoader);
    }

    /**
     * Set the command line arguments.
     *
     * @param args The arguments
     * @return This application
     */
    public Monkey args(@Nullable String... args) {
        if (args != null) {
            this.args = args;
        }
        return this;
    }

    @Override
    public Monkey environments(@Nullable String... environments) {
        return (Monkey) super.environments(environments);
    }

    @Override
    public Monkey packages(@Nullable String... packages) {
        return (Monkey) super.packages(packages);
    }

    /**
     * Maps an exception to the given error code.
     *
     * @param exception The exception
     * @param mapper    The mapper
     * @param <T>       The exception type
     * @return This application
     */
    public <T extends Throwable> Monkey mapError(Class<T> exception, Function<T, Integer> mapper) {
        this.exitHandlers.put(exception, (Function<Throwable, Integer>) mapper);
        return this;
    }

    /**
     * Run the application for the given arguments. Classes for the application will be discovered automatically
     *
     * @param args The arguments
     * @return The {@link ApplicationContext}
     */
    public static Monkey build(String... args) {
        return new Monkey().args(args);
    }

    /**
     * Run the application for the given arguments. Classes for the application will be discovered automatically
     *
     * @param args The arguments
     * @return The {@link ApplicationContext}
     */
    public static ApplicationContext run(String... args) {
        return run(new Class[0], args);
    }

    /**
     * Run the application for the given arguments.
     *
     * @param cls  The application class
     * @param args The arguments
     * @return The {@link ApplicationContext}
     */
    public static ApplicationContext run(Class cls, String... args) {
        return run(new Class[]{cls}, args);
    }

    /**
     * Run the application for the given arguments.
     *
     * @param classes The application classes
     * @param args    The arguments
     * @return The {@link ApplicationContext}
     */
    public static ApplicationContext run(Class[] classes, String... args) {
        return new Monkey()
            .classes(classes)
            .args(args)
            .start();
    }

    /**
     * Default handling of startup exceptions.
     *
     * @param environment The environment
     * @param exception   The exception
     * @throws ApplicationStartupException If the server cannot be shutdown with an appropriate exist code
     */
    protected void handleStartupException(Environment environment, Throwable exception) {
        Function<Throwable, Integer> exitCodeMapper = exitHandlers.computeIfAbsent(exception.getClass(), exceptionType -> (throwable -> 1));
        Integer code = exitCodeMapper.apply(exception);
        if (code > 0) {
            if (!environment.getActiveNames().contains(Environment.TEST)) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Error starting Micronaut server: " + exception.getMessage(), exception);
                }
                System.exit(code);
            }
        }
        throw new ApplicationStartupException("Error starting Micronaut server: " + exception.getMessage(), exception);
    }
}
