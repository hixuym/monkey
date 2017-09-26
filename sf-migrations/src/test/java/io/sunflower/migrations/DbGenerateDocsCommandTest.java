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
import liquibase.Liquibase;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;

@NotThreadSafe
public class DbGenerateDocsCommandTest extends AbstractMigrationTest {

    private DbGenerateDocsCommand<TestMigrationConfiguration> generateDocsCommand = new DbGenerateDocsCommand<>(
        TestMigrationConfiguration::getDataSource, TestMigrationConfiguration.class, "migrations.xml");

    @Test
    public void testRun() throws Exception {
        Liquibase liquibase = Mockito.mock(Liquibase.class);
        generateDocsCommand.run(new Namespace(ImmutableMap.of("output", ImmutableList.of("/tmp/docs"))), liquibase);
        Mockito.verify(liquibase).generateDocumentation("/tmp/docs");
    }

    @Test
    public void testHelpPage() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        createSubparser(generateDocsCommand).printHelp(new PrintWriter(new OutputStreamWriter(baos, UTF_8), true));
        assertThat(baos.toString(UTF_8)).isEqualTo(String.format(
            "usage: db generate-docs [-h] [--migrations MIGRATIONS-FILE]%n" +
                "          [--catalog CATALOG] [--schema SCHEMA] [file] output%n" +
                "%n" +
                "Generate documentation about the database state.%n" +
                "%n" +
                "positional arguments:%n" +
                "  file                   application configuration file%n" +
                "  output                 output directory%n" +
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
