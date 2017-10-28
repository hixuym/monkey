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

import com.codahale.metrics.MetricRegistry;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Test;

import java.sql.SQLFeatureNotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ManagedPooledDataSourceTest {

    private final PoolProperties config = new PoolProperties();
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final ManagedPooledDataSource dataSource = new ManagedPooledDataSource(config,
            metricRegistry);

    @Test
    public void hasNoParentLogger() throws Exception {
        try {
            dataSource.getParentLogger();
            failBecauseExceptionWasNotThrown(SQLFeatureNotSupportedException.class);
        } catch (SQLFeatureNotSupportedException e) {
            assertThat((Object) e).isInstanceOf(SQLFeatureNotSupportedException.class);
        }
    }
}
