package io.sunflower.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.sunflower.setup.Environment;

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

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());

        Server server = new Server();

        environment.lifecycle().attach(server);

        return server;
    }

    @Override
    public void configure(Environment environment) {
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("applicationContextPath", applicationContextPath)
            .toString();
    }
}
