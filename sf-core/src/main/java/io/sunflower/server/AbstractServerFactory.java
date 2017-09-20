package io.sunflower.server;

import com.google.common.io.Resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.validation.constraints.Min;

import io.sunflower.setup.Environment;
import io.sunflower.util.Duration;
import io.sunflower.validation.MinDuration;

/**
 * A base class for {@link ServerFactory} implementations.
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>{@code requestLog}</td>
 * <td></td>
 * <td>The {@link RequestLogFactory request log} configuration.</td>
 * </tr>
 * <tr>
 * <td>{@code gzip}</td>
 * <td></td>
 * <td>The {@link GzipHandlerFactory GZIP} configuration.</td>
 * </tr>
 * <tr>
 * <td>{@code serverPush}</td>
 * <td></td>
 * <td>The {@link ServerPushFilterFactory} configuration.</td>
 * </tr>
 * <tr>
 * <td>{@code maxThreads}</td>
 * <td>1024</td>
 * <td>The maximum number of threads to use for requests.</td>
 * </tr>
 * <tr>
 * <td>{@code minThreads}</td>
 * <td>8</td>
 * <td>The minimum number of threads to use for requests.</td>
 * </tr>
 * <tr>
 * <td>{@code maxQueuedRequests}</td>
 * <td>1024</td>
 * <td>The maximum number of requests to queue before blocking the acceptors.</td>
 * </tr>
 * <tr>
 * <td>{@code idleThreadTimeout}</td>
 * <td>1 minute</td>
 * <td>The amount of time a worker thread can be idle before being stopped.</td>
 * </tr>
 * <tr>
 * <td>{@code nofileSoftLimit}</td>
 * <td>(none)</td>
 * <td>
 * The number of open file descriptors before a soft error is issued. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code nofileHardLimit}</td>
 * <td>(none)</td>
 * <td>
 * The number of open file descriptors before a hard error is issued. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code gid}</td>
 * <td>(none)</td>
 * <td>
 * The group ID to switch to once the connectors have started. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code uid}</td>
 * <td>(none)</td>
 * <td>
 * The user ID to switch to once the connectors have started. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code user}</td>
 * <td>(none)</td>
 * <td>
 * The username to switch to once the connectors have started. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code group}</td>
 * <td>(none)</td>
 * <td>
 * The group to switch to once the connectors have started. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code umask}</td>
 * <td>(none)</td>
 * <td>
 * The umask to switch to once the connectors have started. <b>Requires Jetty's
 * {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code startsAsRoot}</td>
 * <td>(none)</td>
 * <td>
 * Whether or not the Dropwizard application is started as a root user. <b>Requires
 * Jetty's {@code libsetuid.so} on {@code java.library.path}.</b>
 * </td>
 * </tr>
 * <tr>
 * <td>{@code registerDefaultExceptionMappers}</td>
 * <td>true</td>
 * <td>
 * Whether or not the default Jersey ExceptionMappers should be registered.
 * Set this to false if you want to register your own.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code shutdownGracePeriod}</td>
 * <td>30 seconds</td>
 * <td>
 * The maximum time to wait for Jetty, and all Managed instances, to cleanly shutdown
 * before forcibly terminating them.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code allowedMethods}</td>
 * <td>GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH</td>
 * <td>
 * The set of allowed HTTP methods. Others will be rejected with a
 * 405 Method Not Allowed response.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code rootPath}</td>
 * <td>/*</td>
 * <td>
 * The URL pattern relative to {@code applicationContextPath} from which the JAX-RS resources will be served.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code enableThreadNameFilter}</td>
 * <td>true</td>
 * <td>
 * Whether or not to apply the {@code ThreadNameFilter} that adjusts thread names to include the request
 * method and request URI.
 * </td>
 * </tr>
 * </table>
 *
 * @see DefaultServerFactory
 * @see SimpleServerFactory
 */
public abstract class AbstractServerFactory implements ServerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerFactory.class);
    private static final Pattern WINDOWS_NEWLINE = Pattern.compile("\\r\\n?");

    @Min(2)
    private int maxThreads = 1024;

    @Min(1)
    private int minThreads = 8;

    private int maxQueuedRequests = 1024;

    @MinDuration(1)
    private Duration idleThreadTimeout = Duration.minutes(1);

    @JsonProperty
    public int getMaxThreads() {
        return maxThreads;
    }

    @JsonProperty
    public void setMaxThreads(int count) {
        this.maxThreads = count;
    }

    @JsonProperty
    public int getMinThreads() {
        return minThreads;
    }

    @JsonProperty
    public void setMinThreads(int count) {
        this.minThreads = count;
    }

    @JsonProperty
    public int getMaxQueuedRequests() {
        return maxQueuedRequests;
    }

    @JsonProperty
    public void setMaxQueuedRequests(int maxQueuedRequests) {
        this.maxQueuedRequests = maxQueuedRequests;
    }

    @JsonProperty
    public Duration getIdleThreadTimeout() {
        return idleThreadTimeout;
    }

    @JsonProperty
    public void setIdleThreadTimeout(Duration idleThreadTimeout) {
        this.idleThreadTimeout = idleThreadTimeout;
    }

    protected void printBanner(String name) {
        try {
            final String banner = WINDOWS_NEWLINE.matcher(Resources.toString(Resources.getResource("banner.txt"),
                StandardCharsets.UTF_8))
                .replaceAll("\n")
                .replace("\n", String.format("%n"));
            LOGGER.info(String.format("Starting {}%n{}"), name, banner);
        } catch (IllegalArgumentException | IOException ignored) {
            // don't display the banner if there isn't one
            LOGGER.info("Starting {}", name);
        }
    }

    @Override
    public void configure(Environment environment) {

    }
}
