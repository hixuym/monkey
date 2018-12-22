package io.sunflower.db;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.sunflower.configuration.ResourceConfigurationSourceProvider;
import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.Jackson;
import io.sunflower.util.Duration;
import io.sunflower.validation.BaseValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DataSourceFactoryTest {
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    private DataSourceFactory factory;

    @Nullable
    private ManagedDataSource dataSource;

    @Before
    public void setUp() {
        factory = new DataSourceFactory();
        factory.setUrl("jdbc:h2:mem:DbTest-" + System.currentTimeMillis() + ";user=sa");
        factory.setDriverClass("org.h2.Driver");
        factory.setValidationQuery("SELECT 1");
    }

    @After
    public void tearDown() throws Exception {
        if (null != dataSource) {
            dataSource.stop();
        }
    }

    private ManagedDataSource dataSource() throws Exception {
        dataSource = factory.build(metricRegistry, healthCheckRegistry, "test");
        dataSource.start();
        return dataSource;
    }

    @Test
    public void buildsAConnectionPoolToTheDatabase() throws Exception {
        try (Connection connection = dataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        assertThat(set.getInt(1)).isEqualTo(1);
                    }
                }
            }
        }
    }

    @Test
    public void testNoValidationQueryTimeout() throws Exception {
        try (Connection connection = dataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                assertThat(statement.getQueryTimeout()).isEqualTo(0);
            }
        }
    }

    @Ignore
    public void testValidationQueryTimeoutIsSet() throws Exception {
        factory.setValidationQueryTimeout(Duration.seconds(3));

        try (Connection connection = dataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select 1")) {
                assertThat(statement.getQueryTimeout()).isEqualTo(3);
            }
        }
    }

    @Test
    public void invalidJDBCDriverClassThrowsSQLException() {
        final DataSourceFactory factory = new DataSourceFactory();
        factory.setDriverClass("org.example.no.driver.here");

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
            factory.build(metricRegistry, healthCheckRegistry, "test").getConnection());
    }

    @Test
    public void createDefaultFactory() throws Exception {
        final DataSourceFactory factory = new YamlConfigurationFactory<>(DataSourceFactory.class,
            BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw")
            .build(new ResourceConfigurationSourceProvider(), "yaml/minimal_db_pool.yml");

        assertThat(factory.getDriverClass()).isEqualTo("org.postgresql.Driver");
        assertThat(factory.getUser()).isEqualTo("pg-user");
        assertThat(factory.getPassword()).isEqualTo("iAMs00perSecrEET");
        assertThat(factory.getUrl()).isEqualTo("jdbc:postgresql://db.example.com/db-prod");
        assertThat(factory.getValidationQuery()).isEqualTo("/* Health Check */ SELECT 1");
        assertThat(factory.getValidationQueryTimeout()).isEqualTo(Optional.empty());
    }

    @Test
    public void metricsRecorded() throws Exception {
        dataSource();
        Map<String, Gauge> poolMetrics = metricRegistry.getGauges();
        assertThat(poolMetrics.keySet()).contains(
                "test.pool.ActiveConnections",
                "test.pool.IdleConnections",
                "test.pool.MaxConnections",
                "test.pool.MinConnections",
                "test.pool.PendingConnections",
                "test.pool.TotalConnections");
    }

}
