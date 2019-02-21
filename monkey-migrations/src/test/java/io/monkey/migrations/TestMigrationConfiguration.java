package io.monkey.migrations;

import io.monkey.Configuration;
import io.monkey.datasource.DataSourceFactory;

public class TestMigrationConfiguration extends Configuration {

    private DataSourceFactory dataSource;

    public TestMigrationConfiguration(DataSourceFactory dataSource) {
        this.dataSource = dataSource;
    }

    public DataSourceFactory getDataSource() {
        return dataSource;
    }
}
