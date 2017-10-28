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

package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.guice.Mode;
import io.sunflower.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author michael
 */
@JsonTypeName("noops")
public class NoopsServerFactory extends AbstractServerFactory {

    private static Logger logger = LoggerFactory.getLogger(NoopsServerFactory.class);

    @Override
    public Server build(Environment environment) {
        return new Server(environment) {

            @Override
            protected void boot() throws Exception {
                logger.info("do nothing, just use for testing.");
            }

            @Override
            protected void shutdown() throws Exception {
            }
        };
    }

    @Override
    public void configure(Environment environment) {
    }

    @Override
    public Mode getMode() {
        return Mode.test;
    }
}
