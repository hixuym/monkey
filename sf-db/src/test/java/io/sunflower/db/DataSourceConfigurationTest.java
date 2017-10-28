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

import com.google.common.io.Resources;
import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.Jackson;
import io.sunflower.util.Duration;
import io.sunflower.validation.BaseValidator;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DataSourceConfigurationTest {

    @Test
    public void testFullConfiguration() throws Exception {
        DataSourceFactory ds = getDataSourceFactory("yaml/full_db_pool.yml");

        assertThat(ds.getDriverClass()).isEqualTo("org.postgresql.Driver");
        assertThat(ds.getUser()).isEqualTo("pg-user");
        assertThat(ds.getUrl()).isEqualTo("jdbc:postgresql://db.example.com/db-prod");
        assertThat(ds.getPassword()).isEqualTo("iAMs00perSecrEET");
        assertThat(ds.getProperties()).containsEntry("charSet", "UTF-8");
        assertThat(ds.getMaxWaitForConnection()).isEqualTo(Duration.seconds(1));
        assertThat(ds.getValidationQuery()).isEqualTo("/* MyService Health Check */ SELECT 1");
        assertThat(ds.getMinSize()).isEqualTo(8);
        assertThat(ds.getInitialSize()).isEqualTo(15);
        assertThat(ds.getMaxSize()).isEqualTo(32);
        assertThat(ds.getCheckConnectionWhileIdle()).isFalse();
        assertThat(ds.getEvictionInterval()).isEqualTo(Duration.seconds(10));
        assertThat(ds.getMinIdleTime()).isEqualTo(Duration.minutes(1));
        assertThat(ds.getValidationInterval()).isEqualTo(Duration.minutes(1));
        assertThat(ds.isAutoCommentsEnabled()).isFalse();
        assertThat(ds.getReadOnlyByDefault()).isFalse();
        assertThat(ds.isRemoveAbandoned()).isTrue();
        assertThat(ds.getRemoveAbandonedTimeout()).isEqualTo(Duration.seconds(15L));
        assertThat(ds.getAbandonWhenPercentageFull()).isEqualTo(75);
        assertThat(ds.isAlternateUsernamesAllowed()).isTrue();
        assertThat(ds.getCommitOnReturn()).isTrue();
        assertThat(ds.getRollbackOnReturn()).isTrue();
        assertThat(ds.getAutoCommitByDefault()).isFalse();
        assertThat(ds.getDefaultCatalog()).isEqualTo("test_catalog");
        assertThat(ds.getDefaultTransactionIsolation())
                .isEqualTo(DataSourceFactory.TransactionIsolation.READ_COMMITTED);
        assertThat(ds.getUseFairQueue()).isFalse();
        assertThat(ds.getInitializationQuery())
                .isEqualTo("insert into connections_log(ts) values (now())");
        assertThat(ds.getLogAbandonedConnections()).isEqualTo(true);
        assertThat(ds.getLogValidationErrors()).isEqualTo(true);
        assertThat(ds.getMaxConnectionAge()).isEqualTo(Optional.of(Duration.hours(1)));
        assertThat(ds.getCheckConnectionOnBorrow()).isEqualTo(true);
        assertThat(ds.getCheckConnectionOnConnect()).isEqualTo(false);
        assertThat(ds.getCheckConnectionOnReturn()).isEqualTo(true);
        assertThat(ds.getValidationQueryTimeout()).isEqualTo(Optional.of(Duration.seconds(3)));
        assertThat(ds.getValidatorClassName()).isEqualTo(Optional.of("CustomConnectionValidator"));
        assertThat(ds.getJdbcInterceptors())
                .isEqualTo(Optional.of("StatementFinalizer;SlowQueryReport"));
    }

    @Test
    public void testMinimalConfiguration() throws Exception {
        DataSourceFactory ds = getDataSourceFactory("yaml/minimal_db_pool.yml");

        assertThat(ds.getDriverClass()).isEqualTo("org.postgresql.Driver");
        assertThat(ds.getUser()).isEqualTo("pg-user");
        assertThat(ds.getUrl()).isEqualTo("jdbc:postgresql://db.example.com/db-prod");
        assertThat(ds.getPassword()).isEqualTo("iAMs00perSecrEET");
        assertThat(ds.getProperties()).isEmpty();
        assertThat(ds.getMaxWaitForConnection()).isEqualTo(Duration.seconds(30));
        assertThat(ds.getValidationQuery()).isEqualTo("/* Health Check */ SELECT 1");
        assertThat(ds.getMinSize()).isEqualTo(10);
        assertThat(ds.getInitialSize()).isEqualTo(10);
        assertThat(ds.getMaxSize()).isEqualTo(100);
        assertThat(ds.getCheckConnectionWhileIdle()).isTrue();
        assertThat(ds.getEvictionInterval()).isEqualTo(Duration.seconds(5));
        assertThat(ds.getMinIdleTime()).isEqualTo(Duration.minutes(1));
        assertThat(ds.getValidationInterval()).isEqualTo(Duration.seconds(30));
        assertThat(ds.isAutoCommentsEnabled()).isTrue();
        assertThat(ds.getReadOnlyByDefault()).isNull();
        assertThat(ds.isRemoveAbandoned()).isFalse();
        assertThat(ds.getRemoveAbandonedTimeout()).isEqualTo(Duration.seconds(60L));
        assertThat(ds.getAbandonWhenPercentageFull()).isEqualTo(0);
        assertThat(ds.isAlternateUsernamesAllowed()).isFalse();
        assertThat(ds.getCommitOnReturn()).isFalse();
        assertThat(ds.getRollbackOnReturn()).isFalse();
        assertThat(ds.getAutoCommitByDefault()).isNull();
        assertThat(ds.getDefaultCatalog()).isNull();
        assertThat(ds.getDefaultTransactionIsolation())
                .isEqualTo(DataSourceFactory.TransactionIsolation.DEFAULT);
        assertThat(ds.getUseFairQueue()).isTrue();
        assertThat(ds.getInitializationQuery()).isNull();
        assertThat(ds.getLogAbandonedConnections()).isEqualTo(false);
        assertThat(ds.getLogValidationErrors()).isEqualTo(false);
        assertThat(ds.getMaxConnectionAge()).isEqualTo(Optional.empty());
        assertThat(ds.getCheckConnectionOnBorrow()).isEqualTo(false);
        assertThat(ds.getCheckConnectionOnConnect()).isEqualTo(true);
        assertThat(ds.getCheckConnectionOnReturn()).isEqualTo(false);
        assertThat(ds.getValidationQueryTimeout()).isEqualTo(Optional.empty());
    }

    @Test
    public void testInlineUserPasswordConfiguration() throws Exception {
        DataSourceFactory ds = getDataSourceFactory("yaml/inline_user_pass_db_pool.yml");

        assertThat(ds.getDriverClass()).isEqualTo("org.postgresql.Driver");
        assertThat(ds.getUrl())
                .isEqualTo("jdbc:postgresql://db.example.com/db-prod?user=scott&password=tiger");
        assertThat(ds.getUser()).isNull();
        assertThat(ds.getPassword()).isNull();
    }

    @Test
    public void testInitialSizeZeroIsAllowed() throws Exception {
        DataSourceFactory ds = getDataSourceFactory("yaml/empty_initial_pool.yml");
        assertThat(ds.getInitialSize()).isEqualTo(0);
    }

    private DataSourceFactory getDataSourceFactory(String resourceName) throws Exception {
        return new YamlConfigurationFactory<>(DataSourceFactory.class,
                BaseValidator.newValidator(), Jackson.newObjectMapper(), "sf")
                .build(new File(Resources.getResource(resourceName).toURI()));
    }
}
