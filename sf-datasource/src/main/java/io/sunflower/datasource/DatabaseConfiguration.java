package io.sunflower.datasource;

import io.sunflower.Configuration;

public interface DatabaseConfiguration<T extends Configuration> {
    PooledDataSourceFactory getDataSourceFactory(T configuration);
}
