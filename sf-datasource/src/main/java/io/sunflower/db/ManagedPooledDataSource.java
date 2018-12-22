package io.sunflower.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * A {@link ManagedDataSource} which is backed by a Tomcat pooled {@link javax.sql.DataSource}.
 */
public class ManagedPooledDataSource extends HikariDataSource implements ManagedDataSource {

    /**
     * Create a new data source with the given connection pool configuration.
     *
     */
    public ManagedPooledDataSource(HikariConfig hikariConfig) {
        super(hikariConfig);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        close();
    }
}
