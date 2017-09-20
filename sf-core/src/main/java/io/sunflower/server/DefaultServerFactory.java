package io.sunflower.server;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.sunflower.undertow.Server;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;

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

    @Min(2)
    private int adminMaxThreads = 64;

    @Min(1)
    private int adminMinThreads = 1;

    private String applicationContextPath = "";

    private String adminContextPath = "";

    @JsonProperty
    public List<ConnectorFactory> getApplicationConnectors() {
        return applicationConnectors;
    }

    @JsonProperty
    public void setApplicationConnectors(List<ConnectorFactory> connectors) {
        this.applicationConnectors = connectors;
    }

    @JsonProperty
    public List<ConnectorFactory> getAdminConnectors() {
        return adminConnectors;
    }

    @JsonProperty
    public void setAdminConnectors(List<ConnectorFactory> connectors) {
        this.adminConnectors = connectors;
    }

    @JsonProperty
    public int getAdminMaxThreads() {
        return adminMaxThreads;
    }

    @JsonProperty
    public void setAdminMaxThreads(int adminMaxThreads) {
        this.adminMaxThreads = adminMaxThreads;
    }

    @JsonProperty
    public int getAdminMinThreads() {
        return adminMinThreads;
    }

    @JsonProperty
    public void setAdminMinThreads(int adminMinThreads) {
        this.adminMinThreads = adminMinThreads;
    }

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

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());

        Undertow.Builder builder = Undertow.builder()
            .setServerOption(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, true);

        for (ConnectorFactory connectorFactory : applicationConnectors) {

            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            if (Strings.isNullOrEmpty(applicationContextPath)) {
                listenerBuilder.setRootHandler(environment.getApplicationHandler());
            } else {
                listenerBuilder.setRootHandler(new PathHandler().addPrefixPath(
                    applicationContextPath, environment.getApplicationHandler()));
            }

            builder.addListener(listenerBuilder);
        }

        for (ConnectorFactory connectorFactory : adminConnectors) {
            Undertow.ListenerBuilder listenerBuilder = connectorFactory.build();

            if (Strings.isNullOrEmpty(adminContextPath)) {
                listenerBuilder.setRootHandler(environment.admin().getAdminHandler());
            } else {
                listenerBuilder.setRootHandler(new PathHandler().addPrefixPath(
                    adminContextPath, environment.admin().getAdminHandler()
                ));
            }

            builder.addListener(listenerBuilder);
        }

        Server server = new Server(builder.build());

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
            .add("adminMaxThreads", adminMaxThreads)
            .add("adminMinThreads", adminMinThreads)
            .add("applicationContextPath", applicationContextPath)
            .add("adminContextPath", adminContextPath)
            .toString();
    }
}
