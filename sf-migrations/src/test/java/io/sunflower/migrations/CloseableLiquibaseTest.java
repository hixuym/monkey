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

package io.sunflower.migrations;

import com.codahale.metrics.MetricRegistry;

import net.jcip.annotations.NotThreadSafe;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.junit.Before;
import org.junit.Test;

import io.sunflower.db.DataSourceFactory;
import io.sunflower.db.ManagedPooledDataSource;

import static org.assertj.core.api.Assertions.assertThat;

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

        dataSource = (ManagedPooledDataSource) factory.build(new MetricRegistry(), "DbTest");
        liquibase = new CloseableLiquibaseWithClassPathMigrationsFile(dataSource, "migrations.xml");
    }

    @Test
    public void testWhenClosingAllConnectionsInPoolIsReleased() throws Exception {

        ConnectionPool pool = dataSource.getPool();
        assertThat(pool.getActive()).isEqualTo(1);

        liquibase.close();

        assertThat(pool.getActive()).isZero();
        assertThat(pool.getIdle()).isZero();
        assertThat(pool.isClosed()).isTrue();
    }
}