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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

@NotThreadSafe
public class DbTestCommandTest extends AbstractMigrationTest {

  private final DbTestCommand<TestMigrationConfiguration> dbTestCommand = new DbTestCommand<>(
      new TestMigrationDatabaseConfiguration(), TestMigrationConfiguration.class,
      "migrations-ddl.xml");

  @Test
  public void testRun() throws Exception {
    // Apply and rollback some DDL changes
    final TestMigrationConfiguration conf = createConfiguration(getDatabaseUrl());
    dbTestCommand.run(null, new Namespace(ImmutableMap.of()), conf);

    // No exception, the test passed
  }

  @Test
  public void testPrintHelp() throws Exception {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    createSubparser(dbTestCommand)
        .printHelp(new PrintWriter(new OutputStreamWriter(baos, UTF_8), true));
    assertThat(baos.toString(UTF_8)).isEqualTo(String.format(
        "usage: db test [-h] [--migrations MIGRATIONS-FILE] [--catalog CATALOG]%n" +
            "          [--schema SCHEMA] [-i CONTEXTS] [file]%n" +
            "%n" +
            "Apply and rollback pending change sets.%n" +
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
            "  -i CONTEXTS, --include CONTEXTS%n" +
            "                         include change sets from the given context%n"));
  }
}
