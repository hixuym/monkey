package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.sunflower.setup.Environment;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.sunflower.undertow.Server;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

/**
 * A single-connector implementation of {@link ServerFactory}, suitable for PaaS deployments
 * (e.g., Heroku) where applications are limited to a single, runtime-defined port. A startup script
 * can override the port via {@code -Ddw.server.connector.port=$PORT}.
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>{@code connector}</td>
 * <td>An {@link HttpConnectorFactory HTTP connector} listening on port {@code 8080}.</td>
 * <td>The {@link ConnectorFactory connector} which will handle both application and admin requests.</td>
 * </tr>
 * <tr>
 * <td>{@code applicationContextPath}</td>
 * <td>{@code /application}</td>
 * <td>The context path of the application servlets, including Jersey.</td>
 * </tr>
 * <tr>
 * <td>{@code adminContextPath}</td>
 * <td>{@code /admin}</td>
 * <td>The context path of the admin servlets, including metrics and tasks.</td>
 * </tr>
 * </table>
 * <p/>
 * For more configuration parameters, see {@link AbstractServerFactory}.
 *
 * @see ServerFactory
 * @see AbstractServerFactory
 */
@JsonTypeName("simple")
public class SimpleServerFactory extends AbstractServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServerFactory.class);

    @Valid
    @NotNull
    private ConnectorFactory connector = HttpConnectorFactory.application();

    @NotEmpty
    private String applicationContextPath = "/app";

    @NotEmpty
    private String adminContextPath = "/admin";

    @JsonProperty
    public ConnectorFactory getConnector() {
        return connector;
    }

    @JsonProperty
    public void setConnector(ConnectorFactory factory) {
        this.connector = factory;
    }

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(String contextPath) {
        this.applicationContextPath = contextPath;
    }

    @JsonProperty
    public String getAdminContextPath() {
        return adminContextPath;
    }

    @JsonProperty
    public void setAdminContextPath(String contextPath) {
        this.adminContextPath = contextPath;
    }

    @Override
    public Server build(Environment environment) {

        Undertow.ListenerBuilder listenerBuilder = connector.build();

        Undertow.Builder builder = Undertow.builder();

        PathHandler rootHandler = Handlers.path();

        rootHandler.addPrefixPath(applicationContextPath, environment.getApplicationHandler());
        rootHandler.addPrefixPath(adminContextPath, environment.admin().getAdminHandler());

        builder.addListener(listenerBuilder);

        Server server = new Server(builder.build());

        environment.lifecycle().attach(server);

        return server;
    }

    @Override
    public void configure(Environment environment) {
    }
}
