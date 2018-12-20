package io.sunflower.migrations;

import io.sunflower.Configuration;
import io.sunflower.db.DataSourceFactory;

public class TestMigrationConfiguration extends Configuration {

    private DataSourceFactory dataSource;

    public TestMigrationConfiguration(DataSourceFactory dataSource) {
        this.dataSource = dataSource;
    }

    public DataSourceFactory getDataSource() {
        return dataSource;
    }
}
