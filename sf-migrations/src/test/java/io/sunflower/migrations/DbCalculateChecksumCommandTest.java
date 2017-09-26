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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import liquibase.change.CheckSum;

import static org.assertj.core.api.Assertions.assertThat;

@NotThreadSafe
public class DbCalculateChecksumCommandTest extends AbstractMigrationTest {

    private DbCalculateChecksumCommand<TestMigrationConfiguration> migrateCommand = new DbCalculateChecksumCommand<>(
        TestMigrationConfiguration::getDataSource, TestMigrationConfiguration.class, "migrations.xml");

    @Test
    public void testRun() throws Exception {
        final AtomicBoolean checkSumVerified = new AtomicBoolean();
        migrateCommand.setCheckSumConsumer(checkSum -> {
            assertThat(checkSum).isEqualTo(CheckSum.parse("7:3a61a7a72c9ce082b7059215975e6e09"));
            checkSumVerified.set(true);
        });
        migrateCommand.run(null, new Namespace(ImmutableMap.of("id", ImmutableList.of("2"),
            "author", ImmutableList.of("db_dev"))), createConfiguration(getDatabaseUrl()));
        assertThat(checkSumVerified.get()).isTrue();
    }

    @Test
    public void testHelpPage() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        createSubparser(migrateCommand).printHelp(new PrintWriter(new OutputStreamWriter(out, UTF_8), true));
        assertThat(out.toString(UTF_8)).isEqualTo(String.format(
            "usage: db calculate-checksum [-h] [--migrations MIGRATIONS-FILE]%n" +
                "          [--catalog CATALOG] [--schema SCHEMA] [file] id author%n" +
                "%n" +
                "Calculates and prints a checksum for a change set%n" +
                "%n" +
                "positional arguments:%n" +
                "  file                   application configuration file%n" +
                "  id                     change set id%n" +
                "  author                 author name%n" +
                "%n" +
                "named arguments:%n" +
                "  -h, --help             show this help message and exit%n" +
                "  --migrations MIGRATIONS-FILE%n" +
                "                         the file containing  the  Liquibase migrations for%n" +
                "                         the application%n" +
                "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n" +
                "                         default if omitted)%n" +
                "  --schema SCHEMA        Specify the database schema  (use database default%n" +
                "                         if omitted)%n"));
    }
}