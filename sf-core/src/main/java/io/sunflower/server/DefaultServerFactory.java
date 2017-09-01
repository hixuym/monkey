package io.sunflower.server;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.sunflower.setup.Environment;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;

/**
 * The default implementation of {@link ServerFactory}, which allows for multiple sets of
 * application and admin connectors, all running on separate ports. Admin connectors use a separate
 * thread pool to keep the control and data planes separate(ish).
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>{@code applicationListeners}</td>
 * <td>An {@link HttpListenerFactory HTTP connector} listening on port 8080.</td>
 * <td>A set of {@link ListenerFactory connectors} which will handle application requests.</td>
 * </tr>
 * <tr>
 * <td>{@code adminListeners}</td>
 * <td>An {@link HttpListenerFactory HTTP connector} listening on port 8081.</td>
 * <td>A set of {@link ListenerFactory connectors} which will handle admin requests.</td>
 * </tr>
 * <tr>
 * <td>{@code adminMaxThreads}</td>
 * <td>64</td>
 * <td>The maximum number of threads to use for admin requests.</td>
 * </tr>
 * <tr>
 * <td>{@code adminMinThreads}</td>
 * <td>1</td>
 * <td>The minimum number of threads to use for admin requests.</td>
 * </tr>
 * </table>
 * <p/>
 * For more configuration parameters, see {@link AbstractServerFactory}.
 *
 * @see ServerFactory
 * @see AbstractServerFactory
 */
@JsonTypeName("default")
public class DefaultServerFactory extends AbstractServerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerFactory.class);

    @Valid
    @NotNull
    private List<ListenerFactory> applicationListeners = Collections.singletonList(HttpListenerFactory.application());

    @Valid
    @NotNull
    private List<ListenerFactory> adminListeners = Collections.singletonList(HttpListenerFactory.admin());

    @NotEmpty
    private String applicationContextPath = "/";

    @NotEmpty
    private String adminContextPath = "/";

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(final String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    @JsonProperty
    public String getAdminContextPath() {
        return adminContextPath;
    }

    @JsonProperty
    public void setAdminContextPath(final String adminContextPath) {
        this.adminContextPath = adminContextPath;
    }

    @JsonProperty
    public List<ListenerFactory> getApplicationListeners() {
        return applicationListeners;
    }

    @JsonProperty
    public void setApplicationListeners(List<ListenerFactory> applicationListeners) {
        this.applicationListeners = applicationListeners;
    }

    @JsonProperty
    public List<ListenerFactory> getAdminListeners() {
        return adminListeners;
    }

    @JsonProperty
    public void setAdminListeners(List<ListenerFactory> adminListeners) {
        this.adminListeners = adminListeners;
    }

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());

        Undertow.Builder undertowBuilder = Undertow.builder()
            .setIoThreads(getIoThreads())
            .setWorkerThreads(getWorkerThreads())
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        LOGGER.info("Registering ninja handler with root path prefix: {}", applicationContextPath);
        for (ListenerFactory factory : applicationListeners) {
            Undertow.ListenerBuilder listenerBuilder = factory.build();
            listenerBuilder.setRootHandler(createApplicationHandler(applicationContextPath));
            undertowBuilder.addListener(listenerBuilder);
        }

        LOGGER.info("Registering admin handler with root path prefix: {}", adminContextPath);
        for (ListenerFactory factory : adminListeners) {
            Undertow.ListenerBuilder listenerBuilder = factory.build();
            listenerBuilder.setRootHandler(createAdminHandler(adminContextPath));
            undertowBuilder.addListener(listenerBuilder);
        }

        return new Server(undertowBuilder.build());
    }

    @Override
    public void configure(Environment environment) {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("applicationListeners", applicationListeners)
            .add("adminListeners", adminListeners)
            .add("applicationContextPath", applicationContextPath)
            .add("adminContextPath", adminContextPath)
            .toString();
    }
}
