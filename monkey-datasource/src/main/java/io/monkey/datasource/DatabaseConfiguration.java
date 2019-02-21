package io.monkey.datasource;

import io.monkey.Configuration;

public interface DatabaseConfiguration<T extends Configuration> {
    PooledDataSourceFactory getDataSourceFactory(T configuration);
}
