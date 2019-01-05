package io.monkey.jdbi;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jdbi3.InstrumentedTimingCollector;
import io.monkey.datasource.ManagedDataSource;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.lifecycle.setup.LifecycleEnvironment;
import io.monkey.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlStatements;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class JdbiFactoryTest {
    @Test
    public void testBuild() {
        final Environment environment = mock(Environment.class);
        final MetricRegistry metrics = mock(MetricRegistry.class);
        final LifecycleEnvironment lifecycle = mock(LifecycleEnvironment.class);
        final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
        final PooledDataSourceFactory configuration = mock(PooledDataSourceFactory.class);
        final String name = UUID.randomUUID().toString();
        final ManagedDataSource dataSource = mock(ManagedDataSource.class);
        final String validationQuery = UUID.randomUUID().toString();
        final Jdbi jdbi = mock(Jdbi.class);
        final SqlStatements sqlStatements = new SqlStatements();

        when(environment.metrics()).thenReturn(metrics);
        when(environment.lifecycle()).thenReturn(lifecycle);
        when(environment.healthChecks()).thenReturn(healthChecks);

        when(configuration.build(metrics, healthChecks)).thenReturn(dataSource);
        when(configuration.getValidationQuery()).thenReturn(validationQuery);
        when(configuration.isAutoCommentsEnabled()).thenReturn(true);

        when(jdbi.getConfig(SqlStatements.class)).thenReturn(sqlStatements);

        final JdbiFactory factory = Mockito.spy(new JdbiFactory());

        when(factory.newInstance(dataSource)).thenReturn(jdbi);

        final Jdbi result = factory.build(environment, configuration);

        assertThat(result).isSameAs(jdbi);
        verify(lifecycle).manage(dataSource);
        verify(jdbi).setTimingCollector(any(InstrumentedTimingCollector.class));
        verify(jdbi).setTemplateEngine(any(NamePrependingTemplateEngine.class));
        verify(factory).configure(jdbi);
    }
}
