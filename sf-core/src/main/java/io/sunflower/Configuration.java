package io.sunflower;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.sunflower.logging.DefaultLoggingFactory;
import io.sunflower.logging.LoggingFactory;
import io.sunflower.metrics.MetricsFactory;
import io.sunflower.server.DefaultServerFactory;
import io.sunflower.server.ServerFactory;

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
 * Sunflower will parse the given YAML file and provide an {@code ExampleConfiguration} instance
 * to your application whose {@code getName()} method will return {@code "Random Person"} and whose
 * {@code getAge()} method will return {@code 43}.
 *
 * @see <a href="http://www.yaml.org/YAML_for_ruby.html">YAML Cookbook</a>
 */
public class Configuration {

    @Valid
    private LoggingFactory logging;

    @Valid
    private ServerFactory server = new DefaultServerFactory();

    @Valid
    @NotNull
    private MetricsFactory metrics = new MetricsFactory();

    @JsonProperty("undertow")
    public ServerFactory getServerFactory() {
        return server;
    }

    @JsonProperty("undertow")
    public void setServerFactory(ServerFactory server) {
        this.server = server;
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
        return metrics;
    }

    @JsonProperty("metrics")
    public void setMetricsFactory(MetricsFactory metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("logging", logging)
            .add("metrics", metrics)
            .toString();
    }
}
