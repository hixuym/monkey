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

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

@NotThreadSafe
public class DbStatusCommandTest extends AbstractMigrationTest {

    private final DbStatusCommand<TestMigrationConfiguration> statusCommand =
            new DbStatusCommand<>(new TestMigrationDatabaseConfiguration(),
                    TestMigrationConfiguration.class, "migrations.xml");
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private TestMigrationConfiguration conf;

    @Before
    public void setUp() throws Exception {
        conf = createConfiguration(getDatabaseUrl());

        statusCommand.setOutputStream(new PrintStream(baos));
    }

    @Test
    public void testRunOnMigratedDb() throws Exception {
        final String existedDbPath = new File(Resources.getResource("test-db.mv.db").toURI())
                .getAbsolutePath();
        final String existedDbUrl = "jdbc:h2:" + StringUtils.removeEnd(existedDbPath, ".mv.db");
        final TestMigrationConfiguration existedDbConf = createConfiguration(existedDbUrl);

        statusCommand.run(null, new Namespace(ImmutableMap.of()), existedDbConf);
        assertThat(baos.toString(UTF_8)).matches("\\S+ is up to date" + System.lineSeparator());
    }

    @Test
    public void testRun() throws Exception {
        statusCommand.run(null, new Namespace(ImmutableMap.of()), conf);
        assertThat(baos.toString(UTF_8)).matches(
                "3 change sets have not been applied to \\S+" + System.lineSeparator());
    }

    @Test
    public void testVerbose() throws Exception {
        statusCommand.run(null, new Namespace(ImmutableMap.of("verbose", (Object) true)), conf);
        assertThat(baos.toString(UTF_8)).matches(
                "3 change sets have not been applied to \\S+" + System.lineSeparator() +
                        "\\s*migrations\\.xml::1::db_dev" + System.lineSeparator() +
                        "\\s*migrations\\.xml::2::db_dev" + System.lineSeparator() +
                        "\\s*migrations\\.xml::3::db_dev" + System.lineSeparator());
    }

    @Test
    public void testPrintHelp() throws Exception {
        createSubparser(statusCommand)
                .printHelp(new PrintWriter(new OutputStreamWriter(baos, UTF_8), true));
        assertThat(baos.toString(UTF_8)).isEqualTo(String.format(
                "usage: db status [-h] [--migrations MIGRATIONS-FILE] [--catalog CATALOG]%n" +
                        "          [--schema SCHEMA] [-v] [-i CONTEXTS] [file]%n" +
                        "%n" +
                        "Check for pending change sets.%n" +
                        "%n" +
                        "positional arguments:%n" +
                        "  file                   application configuration file%n" +
                        "%n" +
                        "named arguments:%n" +
                        "  -h, --help             show this help message and exit%n" +
                        "  --migrations MIGRATIONS-FILE%n" +
                        "                         the file containing  the  Liquibase migrations for%n" +
                        "                         the application%n" +
                        "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n" +
                        "                         default if omitted)%n" +
                        "  --schema SCHEMA        Specify the database schema  (use database default%n" +
                        "                         if omitted)%n" +
                        "  -v, --verbose          Output verbose information%n" +
                        "  -i CONTEXTS, --include CONTEXTS%n" +
                        "                         include change sets from the given context%n"));
    }
}
