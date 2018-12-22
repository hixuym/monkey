package io.sunflower.migrations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.sunflower.datasource.DataSourceFactory;
import io.sunflower.datasource.ManagedPooledDataSource;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Ignore;

@NotThreadSafe
public class CloseableLiquibaseTest {

    CloseableLiquibase liquibase;
    ManagedPooledDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        DataSourceFactory factory = new DataSourceFactory();

        factory.setDriverClass(org.h2.Driver.class.getName());
        factory.setUrl("jdbc:h2:mem:DbTest-" + System.currentTimeMillis());
        factory.setUser("DbTest");

        dataSource = (ManagedPooledDataSource) factory.build(new MetricRegistry(), new HealthCheckRegistry(), "DbTest");
        liquibase = new CloseableLiquibaseWithClassPathMigrationsFile(dataSource, "migrations.xml");
    }

    @Ignore
    public void testWhenClosingAllConnectionsInPoolIsReleased() throws Exception {

//        ConnectionPool pool = dataSource.getPool();
//        assertThat(pool.getActive()).isEqualTo(1);
//
//        liquibase.close();
//
//        assertThat(pool.getActive()).isZero();
//        assertThat(pool.getIdle()).isZero();
//        assertThat(pool.isClosed()).isTrue();
    }
}
