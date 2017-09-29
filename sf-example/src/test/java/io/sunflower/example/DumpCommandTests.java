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

package io.sunflower.example;

import org.junit.Test;

import io.sunflower.example.cli.DumpConfigCommand;
import io.sunflower.setup.Bootstrap;
import io.sunflower.testing.BaseCommandTest;

public class DumpCommandTests extends BaseCommandTest {
    @Override
    protected Bootstrap getBootstrap() {

        Bootstrap<ExampleConfiguration> bootstrap = new Bootstrap<>(new ExampleApplication());

        bootstrap.addCommand(new DumpConfigCommand());
        return bootstrap;
    }


    @Test
    public void testDump() throws Exception {
        cli.run("dump");
    }
}
