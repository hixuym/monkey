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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import liquibase.Liquibase;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DbMigrateCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

    private PrintStream outputStream = System.out;

    @VisibleForTesting
    void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public DbMigrateCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass,
                            String migrationsFileName) {
        super("migrate", "Apply all pending change sets.", strategy, configurationClass,
                migrationsFileName);
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);

        subparser.addArgument("-n", "--dry-run")
                .action(Arguments.storeTrue())
                .dest("dry-run")
                .setDefault(Boolean.FALSE)
                .help("output the DDL to stdout, don't run it");

        subparser.addArgument("-c", "--count")
                .type(Integer.class)
                .dest("count")
                .help("only apply the next N change sets");

        subparser.addArgument("-i", "--include")
                .action(Arguments.append())
                .dest("contexts")
                .help("include change sets from the given context");
    }

    @Override
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void run(Namespace namespace, Liquibase liquibase) throws Exception {
        final String context = getContext(namespace);
        final Integer count = namespace.getInt("count");
        final boolean dryRun = MoreObjects.firstNonNull(namespace.getBoolean("dry-run"), false);
        if (count != null) {
            if (dryRun) {
                liquibase
                        .update(count, context, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            } else {
                liquibase.update(count, context);
            }
        } else {
            if (dryRun) {
                liquibase.update(context, new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            } else {
                liquibase.update(context);
            }
        }
    }

    private String getContext(Namespace namespace) {
        final List<Object> contexts = namespace.getList("contexts");
        if (contexts == null) {
            return "";
        }
        return Joiner.on(',').join(contexts);
    }
}
