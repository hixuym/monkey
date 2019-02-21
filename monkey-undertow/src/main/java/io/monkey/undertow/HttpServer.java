/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.undertow;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.monkey.server.Server;
import io.monkey.setup.Environment;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpServer extends Server {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final String version;
    private boolean started;
    private Undertow undertow;

    public HttpServer(Environment environment, Undertow undertow) {
        super(environment);
        this.undertow = undertow;
        this.version = Undertow.class.getPackage().getImplementationVersion();
    }

    @Override
    protected void boot() {
        undertow.start();
        started = true;

        List<Undertow.ListenerInfo> listeners = undertow.getListenerInfo();

        List<String> strings = Lists.newArrayList();

        for (Undertow.ListenerInfo info : listeners) {
            strings.add(info.getProtcol() + "://" + info.getAddress());
        }

        logger.info("Started HTTP Server({}) With Connectors:", version);
        logger.info("   {}", Joiner.on("\n").join(strings));
    }

    @Override
    protected void shutdown() {
        if (undertow != null && started) {
            undertow.stop();
            logger.info("Stopped HTTP Server({})", version);
        }
    }

}
