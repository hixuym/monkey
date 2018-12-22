package io.sunflower.db;

import io.sunflower.Configuration;

public interface DatabaseConfiguration<T extends Configuration> {
    PooledDataSourceFactory getDataSourceFactory(T configuration);
}
