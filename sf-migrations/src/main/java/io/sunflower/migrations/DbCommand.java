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

import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.util.SortedMap;
import java.util.TreeMap;

public class DbCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

    private static final String COMMAND_NAME_ATTR = "subcommand";
    private final SortedMap<String, AbstractLiquibaseCommand<T>> subcommands;

    public DbCommand(String name, DatabaseConfiguration<T> strategy, Class<T> configurationClass,
                     String migrationsFileName) {
        super(name, "Run database migration tasks", strategy, configurationClass, migrationsFileName);
        this.subcommands = new TreeMap<>();
        addSubcommand(
                new DbCalculateChecksumCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbClearChecksumsCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbDropAllCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbDumpCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbFastForwardCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbGenerateDocsCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbLocksCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbMigrateCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbPrepareRollbackCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbRollbackCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbStatusCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbTagCommand<>(strategy, configurationClass, migrationsFileName));
        addSubcommand(new DbTestCommand<>(strategy, configurationClass, migrationsFileName));
    }

    private void addSubcommand(AbstractLiquibaseCommand<T> subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }

    @Override
    public void configure(Subparser subparser) {
        for (AbstractLiquibaseCommand<T> subcommand : subcommands.values()) {
            final Subparser cmdParser = subparser.addSubparsers()
                    .addParser(subcommand.getName())
                    .setDefault(COMMAND_NAME_ATTR, subcommand.getName())
                    .description(subcommand.getDescription());
            subcommand.configure(cmdParser);
        }
    }

    @Override
    public void run(Namespace namespace, Liquibase liquibase) throws Exception {
        final AbstractLiquibaseCommand<T> subcommand = subcommands
                .get(namespace.getString(COMMAND_NAME_ATTR));
        subcommand.run(namespace, liquibase);
    }
}
