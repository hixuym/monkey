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

import io.sunflower.db.ManagedDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

import java.sql.SQLException;

/**
 * @author michael
 */
public abstract class CloseableLiquibase extends Liquibase implements AutoCloseable {

    private final ManagedDataSource dataSource;

    CloseableLiquibase(
            String changeLogFile,
            ResourceAccessor resourceAccessor,
            Database database,
            ManagedDataSource dataSource
    ) throws LiquibaseException, SQLException {
        super(changeLogFile, resourceAccessor, database);
        this.dataSource = dataSource;
    }

    public CloseableLiquibase(String changeLogFile, ResourceAccessor resourceAccessor,
                              DatabaseConnection conn, ManagedDataSource dataSource)
            throws LiquibaseException, SQLException {
        super(changeLogFile, resourceAccessor, conn);
        this.dataSource = dataSource;
    }

    @Override
    public void close() throws Exception {
        try {
            database.close();
        } finally {
            dataSource.stop();
        }
    }
}
