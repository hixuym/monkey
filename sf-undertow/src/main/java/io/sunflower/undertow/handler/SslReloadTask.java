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

package io.sunflower.undertow.handler;

import com.google.common.collect.ImmutableMultimap;
import io.sunflower.undertow.SslReload;
import io.sunflower.undertow.ssl.SslContextFactory;

import java.io.PrintWriter;

/**
 * A task that will refresh all ssl factories with up to date certificate information
 */
public class SslReloadTask extends Task {

    private final SslReload reloader;

    public SslReloadTask(SslReload reloader) {
        super("reload-ssl");
        this.reloader = reloader;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output)
            throws Exception {
        // Iterate through all the reloaders first to ensure valid configuration
        reloader.reload(new SslContextFactory());

        // Now we know that configuration is valid, reload for real
        reloader.reload();

        output.write("Reloaded certificate configuration\n");
    }
}

