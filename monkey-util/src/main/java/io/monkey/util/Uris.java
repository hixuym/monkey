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

package io.monkey.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author michael
 */
public abstract class Uris {

    static private final String URI_SCHEME_CLASSPATH = "classpath";

    public static InputStream openStream(URI uri) throws IOException {
        if (uri.getScheme().equals(URI_SCHEME_CLASSPATH)) {
            String resourceName = uri.getPath();

            InputStream stream = Uris.class.getResourceAsStream(resourceName);

            if (stream == null) {
                throw new IOException("Resource '" + resourceName + "' not found on classpath");
            }

            return stream;
        } else {
            URL url = uri.toURL();

            return url.openStream();
        }
    }
}
