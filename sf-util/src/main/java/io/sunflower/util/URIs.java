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

package io.sunflower.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public abstract class URIs {

    static private final String URI_SCHEME_CLASSPATH = "classpath";

    static private final Logger log = LoggerFactory.getLogger(URIs.class);

    public static InputStream openStream(URI uri) throws IOException {
        if (uri.getScheme().equals(URI_SCHEME_CLASSPATH)) {
            String resourceName = uri.getPath();

            log.debug("Opening keystore on classpath with resource {}", resourceName);

            InputStream stream = URIs.class.getResourceAsStream(resourceName);

            if (stream == null) {
                throw new IOException("Resource '" + resourceName + "' not found on classpath");
            }

            return stream;
        } else {
            URL url = uri.toURL();

            log.debug("Opening url {}", url);

            return url.openStream();
        }
    }
}
