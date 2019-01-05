/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.ebean;

import io.ebean.EbeanServer;
import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import io.ebeaninternal.api.SpiEbeanServer;
import io.monkey.MonkeyException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Michael
 * Created at: 2019/1/5 22:59
 */
public class FlywayMigrationEngine {

    private final EbeanServer ebeanServer;
    private final String application;
    private String baseMigrationPath;

    public FlywayMigrationEngine(EbeanServer ebeanServer, String application) {
        this.ebeanServer = ebeanServer;
        this.application = application;
        this.baseMigrationPath = System.getProperty("user.home");
    }

    public void migrate() {
        FluentConfiguration configuration = Flyway.configure();
        DataSource dataSource = ((SpiEbeanServer) ebeanServer).getServerConfig().getDataSource();
        configuration.dataSource(dataSource);
        configuration.locations("filesystem:" + Paths.get(baseMigrationPath, "dbmigration").toString());

        Flyway flyway = new Flyway(configuration);

        flyway.clean();

        flyway.migrate();
    }


    public void generate() {
        DbMigration dbMigration = DbMigration.create();

        dbMigration.setServer(ebeanServer);
        dbMigration.setApplyPrefix("V");
        dbMigration.setName("auto");
        dbMigration.setPlatform(Platform.H2);
        dbMigration.setPathToResources(this.baseMigrationPath);

        try {
            dbMigration.generateMigration();
        } catch (IOException e) {
            throw new MonkeyException("error generate migration.", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(Paths.get(System.getProperty("user.home"), "app","sqls", "h2"));
    }
}
