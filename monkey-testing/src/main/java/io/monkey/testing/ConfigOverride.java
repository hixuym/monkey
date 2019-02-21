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

package io.monkey.testing;

import java.util.function.Supplier;

/**
 * An override for a field in monkey configuration intended for use with {@link
 * MonkeyAppRule}. <p> Given a configuration file containing
 * <pre>
 * ---
 * server:
 *   applicationConnectors:
 *     - type: http
 *       port: 8000
 *   adminConnectors:
 *     - type: http
 *       port: 8001
 *
 * logging:
 *   loggers:
 *     com.quickstarters.foo: INFO
 * </pre>
 * <ul> <li><code>ConfigOverride.config("debug", "true")</code> will add a top level field named
 * "debug" mapped to the string "true".</li> <li><code>ConfigOverride.config("server.applicationConnectors[0].type",
 * "https")</code> will change the sole application connector to have type "https" instead of type
 * "http". <li><code>ConfigOverride.config("logging.loggers.com\\.quickstarters\\.bar", "DEBUG")</code>
 * will add a logger with the name "com.quickstarters.bar" configured for debug logging.</li> </ul>
 * @author michael
 */
public class ConfigOverride {

    public static final String DEFAULT_PREFIX = "mk.";
    private final String key;
    private final Supplier<String> value;
    private final String propertyPrefix;

    private ConfigOverride(String propertyPrefix, String key, Supplier<String> value) {
        this.key = key;
        this.value = value;
        this.propertyPrefix = propertyPrefix.endsWith(".") ? propertyPrefix : propertyPrefix + ".";
    }

    public static ConfigOverride config(String key, String value) {
        return new ConfigOverride(DEFAULT_PREFIX, key, () -> value);
    }

    public static ConfigOverride config(String propertyPrefix, String key, String value) {
        return new ConfigOverride(propertyPrefix, key, () -> value);
    }

    public static ConfigOverride config(String key, Supplier<String> value) {
        return new ConfigOverride(DEFAULT_PREFIX, key, value);
    }

    public static ConfigOverride config(String propertyPrefix, String key, Supplier<String> value) {
        return new ConfigOverride(propertyPrefix, key, value);
    }

    public void addToSystemProperties() {
        System.setProperty(propertyPrefix + key, value.get());
    }

    public void removeFromSystemProperties() {
        System.clearProperty(propertyPrefix + key);
    }
}
