package io.monkey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.monkey.logging.DefaultLoggingFactory;
import io.monkey.logging.LoggingFactory;
import io.monkey.metrics.MetricsFactory;
import io.monkey.server.ServerFactory;
import io.monkey.server.SimpleServerFactory;

import javax.validation.Valid;

/**
 * An object representation of the YAML configuration file. Extend this with your own configuration
 * properties, and they'll be parsed from the YAML file as well.
 * <p/>
 * For example, given a YAML file with this:
 * <pre>
 * name: "Random Person"
 * age: 43
 * # ... etc ...
 * </pre>
 * And a configuration like this:
 * <pre>
 * public class ExampleConfiguration extends Configuration {
 *     \@NotNull
 *     private String name;
 *
 *     \@Min(1)
 *     \@Max(120)
 *     private int age;
 *
 *     \@JsonProperty
 *     public String getName() {
 *         return name;
 *     }
 *
 *     \@JsonProperty
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *
 *     \@JsonProperty
 *     public int getAge() {
 *         return age;
 *     }
 *
 *     \@JsonProperty
 *     public void setAge(int age) {
 *         this.age = age;
 *     }
 * }
 * </pre>
 * <p/>
 * Dropwizard will parse the given YAML file and provide an {@code ExampleConfiguration} instance to
 * your application whose {@code getName()} method will return {@code "Random Person"} and whose
 * {@code getAge()} method will return {@code 43}.
 *
 * @author michael
 * @see <a href="http://www.yaml.org/YAML_for_ruby.html">YAML Cookbook</a>
 */
public class Configuration {

    @Valid
    private ServerFactory serverFactory;

    @Valid
    private LoggingFactory logging;

    @Valid
    private MetricsFactory metrics;

    /**
     * Returns the server-specific section of the configuration file.
     *
     * @return server-specific configuration parameters
     */
    @JsonProperty("server")
    public ServerFactory getServerFactory() {
        if (serverFactory == null) {
            this.serverFactory = new SimpleServerFactory();
        }
        return serverFactory;
    }

    /**
     * Sets the HTTP-specific section of the configuration file.
     */
    @JsonProperty("server")
    public void setServerFactory(ServerFactory factory) {
        this.serverFactory = factory;
    }

    /**
     * Returns the logging-specific section of the configuration file.
     *
     * @return logging-specific configuration parameters
     */
    @JsonProperty("logging")
    public synchronized LoggingFactory getLoggingFactory() {
        if (logging == null) {
            // Lazy init to avoid a hard dependency to logback
            logging = new DefaultLoggingFactory();
        }
        return logging;
    }

    /**
     * Sets the logging-specific section of the configuration file.
     */
    @JsonProperty("logging")
    public synchronized void setLoggingFactory(LoggingFactory factory) {
        this.logging = factory;
    }

    @JsonProperty("metrics")
    public MetricsFactory getMetricsFactory() {
        if (this.metrics == null) {
            this.metrics = new MetricsFactory();
        }

        return metrics;
    }

    @JsonProperty("metrics")
    public void setMetricsFactory(MetricsFactory metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("server", serverFactory)
                .add("logging", logging)
                .add("metrics", metrics)
                .toString();
    }
}
