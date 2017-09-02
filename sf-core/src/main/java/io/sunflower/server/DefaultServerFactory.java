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
import io.sunflower.undertow.HttpConnectorFactory;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.Server;
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
 * <td>{@code applicationConnectors}</td>
 * <td>An {@link HttpConnectorFactory HTTP connector} listening on port 8080.</td>
 * <td>A set of {@link ConnectorFactory connectors} which will handle application requests.</td>
 * </tr>
 * <tr>
 * <td>{@code adminConnectors}</td>
 * <td>An {@link HttpConnectorFactory HTTP connector} listening on port 8081.</td>
 * <td>A set of {@link ConnectorFactory connectors} which will handle admin requests.</td>
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
    private List<ConnectorFactory> applicationConnectors = Collections.singletonList(HttpConnectorFactory.application());

    @Valid
    @NotNull
    private List<ConnectorFactory> adminConnectors = Collections.singletonList(HttpConnectorFactory.admin());

    @NotEmpty
    private String applicationContextPath = "/";

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(final String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    @JsonProperty
    public List<ConnectorFactory> getApplicationConnectors() {
        return applicationConnectors;
    }

    @JsonProperty
    public void setApplicationConnectors(List<ConnectorFactory> applicationConnectors) {
        this.applicationConnectors = applicationConnectors;
    }

    @JsonProperty
    public List<ConnectorFactory> getAdminConnectors() {
        return adminConnectors;
    }

    @JsonProperty
    public void setAdminConnectors(List<ConnectorFactory> adminConnectors) {
        this.adminConnectors = adminConnectors;
    }

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());

        Undertow.Builder undertowBuilder = Undertow.builder()
            .setIoThreads(getIoThreads())
            .setWorkerThreads(getWorkerThreads())
            // NOTE: should ninja not use equals chars within its cookie values?
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        LOGGER.info("Registering application handler with root path prefix: {}", applicationContextPath);
        for (ConnectorFactory factory : applicationConnectors) {
            Undertow.ListenerBuilder listenerBuilder = factory.build();
            listenerBuilder.setRootHandler(environment.getApplicationContext());
            undertowBuilder.addListener(listenerBuilder);
        }

        for (ConnectorFactory factory : adminConnectors) {
            Undertow.ListenerBuilder listenerBuilder = factory.build();
            listenerBuilder.setRootHandler(environment.getAdminContext());
            undertowBuilder.addListener(listenerBuilder);
        }

        Server server = new Server(undertowBuilder.build());

        environment.lifecycle().attach(server);

        return server;
    }

    @Override
    public void configure(Environment environment) {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("applicationConnectors", applicationConnectors)
            .add("adminConnectors", adminConnectors)
            .add("applicationContextPath", applicationContextPath)
            .toString();
    }
}
