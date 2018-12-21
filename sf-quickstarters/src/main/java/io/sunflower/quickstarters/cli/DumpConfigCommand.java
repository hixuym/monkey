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

package io.sunflower.quickstarters.cli;

import io.sunflower.cli.Cli;
import io.sunflower.cli.ConfiguredCommand;
import io.sunflower.quickstarters.QuickstartersConfiguration;
import io.sunflower.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author michael
 */
public class DumpConfigCommand extends ConfiguredCommand<QuickstartersConfiguration> {

    public DumpConfigCommand() {
        super("dump", "dump configuration.");
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, QuickstartersConfiguration configuration) {
        System.out.println("dump..");
    }

    @Override
    public void onError(Cli cli, Namespace namespace, Throwable e) {
        System.out.println(e.toString());
    }
}
