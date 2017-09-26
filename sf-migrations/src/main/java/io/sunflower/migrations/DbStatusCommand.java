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

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.sunflower.Configuration;
import io.sunflower.db.DatabaseConfiguration;
import liquibase.Liquibase;

public class DbStatusCommand<T extends Configuration> extends AbstractLiquibaseCommand<T> {

    private PrintStream outputStream = System.out;

    @VisibleForTesting
    void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public DbStatusCommand(DatabaseConfiguration<T> strategy, Class<T> configurationClass, String migrationsFileName) {
        super("status", "Check for pending change sets.", strategy, configurationClass, migrationsFileName);
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);

        subparser.addArgument("-v", "--verbose")
            .action(Arguments.storeTrue())
            .dest("verbose")
            .help("Output verbose information");
        subparser.addArgument("-i", "--include")
            .action(Arguments.append())
            .dest("contexts")
            .help("include change sets from the given context");
    }

    @Override
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public void run(Namespace namespace, Liquibase liquibase) throws Exception {
        liquibase.reportStatus(MoreObjects.firstNonNull(namespace.getBoolean("verbose"), false),
            getContext(namespace),
            new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    private String getContext(Namespace namespace) {
        final List<Object> contexts = namespace.getList("contexts");
        if (contexts == null) {
            return "";
        }
        return Joiner.on(',').join(contexts);
    }
}
