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

import io.monkey.MonkeyException;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * author michael
 */
public class PathHandlerCollector {

    private static Logger logger = LoggerFactory.getLogger(PathHandlerCollector.class);

    private Map<String, HttpHandler> handlerMap;

    private volatile HttpHandler defaultHandler;

    @Inject
    public PathHandlerCollector(Map<String, HttpHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public HttpHandler buildApplicationHandler() {

        if (defaultHandler == null && handlerMap.isEmpty()) {
            throw new MonkeyException("no http handler found, please add your handlers by PathHandlerCollector.");
        }

        if (defaultHandler == null) {
            logger.warn("http server have no handler for /.");
        }

        PathHandler handler = new PathHandler(50);

        if (defaultHandler != null) {
            handler.addPrefixPath("/", defaultHandler);
        }

        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            handler.addPrefixPath(entry.getKey(), entry.getValue());
        }

        return handler;
    }


}
