/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.db;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * A {@link ManagedDataSource} which is backed by a Tomcat pooled {@link javax.sql.DataSource}.
 */
public class ManagedPooledDataSource extends DataSourceProxy implements ManagedDataSource {
    private final MetricRegistry metricRegistry;

    /**
     * Create a new data source with the given connection pool configuration.
     *
     * @param config the connection pool configuration
     */
    public ManagedPooledDataSource(PoolConfiguration config, MetricRegistry metricRegistry) {
        super(config);
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Doesn't use java.util.logging");
    }

    @Override
    public void start() throws Exception {
        final ConnectionPool connectionPool = createPool();
        metricRegistry.register(name(getClass(), connectionPool.getName(), "active"),
            (Gauge<Integer>) connectionPool::getActive);

        metricRegistry.register(name(getClass(), connectionPool.getName(), "idle"),
            (Gauge<Integer>) connectionPool::getIdle);

        metricRegistry.register(name(getClass(), connectionPool.getName(), "waiting"),
            (Gauge<Integer>) connectionPool::getWaitCount);

        metricRegistry.register(name(getClass(), connectionPool.getName(), "size"),
            (Gauge<Integer>) connectionPool::getSize);
    }

    @Override
    public void stop() throws Exception {
        close();
    }
}
