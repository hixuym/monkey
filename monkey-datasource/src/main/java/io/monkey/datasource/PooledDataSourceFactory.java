package io.monkey.datasource;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.monkey.setup.Environment;
import io.monkey.util.Duration;

import java.util.Map;
import java.util.Optional;

/**
 * Interface of a factory that produces JDBC data sources
 * backed by the connection pool.
 */
public interface PooledDataSourceFactory {

    /**
     *
     * @return default db
     */
    boolean isDefault();

    /**
     *
     * @return db name
     */
    String getDatabaseName();

    /**
     * Whether ORM tools allowed to add comments to SQL queries.
     *
     * @return {@code true}, if allowed
     */
    boolean isAutoCommentsEnabled();

    /**
     * Returns the configuration properties for ORM tools.
     *
     * @return configuration properties as a map
     */
    Map<String, String> getProperties();

    /**
     * Returns the timeout for awaiting a response from the database
     * during connection health checks.
     *
     * @return the timeout as {@code Duration}
     */
    Optional<Duration> getValidationQueryTimeout();

    /**
     * Returns the SQL query, which is being used for the database
     * connection health check.
     *
     * @return the SQL query as a string
     */
    String getValidationQuery();

    /**
     * Returns the Java class of the database driver.
     *
     * @return the JDBC driver class as a string
     */
    String getDriverClass();

    /**
     * Returns the JDBC connection URL.
     *
     * @return the JDBC connection URL as a string
     */
    String getUrl();

    /**
     * Configures the pool as a single connection pool.
     * It's useful for tools that use only one database connection,
     * such as database migrations.
     */
    void asSingleConnectionPool();

    /**
     * Builds a new JDBC data source backed by the connection pool
     * and managed by Dropwizard.
     *
     * @param environment the application environment
     * @return a new JDBC data source as {@code ManagedDataSource}
     */
    ManagedDataSource build(Environment environment);
}
