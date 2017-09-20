/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Helper utilities for working with standalone applications.
 */
public class ServerHelper {
    static private final Logger log = LoggerFactory.getLogger(ServerHelper.class);

    static private final String URI_SCHEME_CLASSPATH = "classpath";

    static public int findAvailablePort(int min, int max) {
        for (int port = min; port < max; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Must already be taken
            }
        }
        throw new IllegalStateException(
            "Could not find available port in range " + min + " to " + max);
    }

    static public InputStream openKeyStoreInput(URI uri) throws IOException {
        if (uri.getScheme().equals(URI_SCHEME_CLASSPATH)) {
            String resourceName = uri.getPath();

            log.debug("Opening keystore on classpath with resource {}", resourceName);

            InputStream stream = ServerHelper.class.getResourceAsStream(resourceName);

            if (stream == null) {
                throw new IOException("Resource '" + resourceName + "' not found on classpath");
            }

            return stream;
        } else {
            URL url = uri.toURL();

            log.debug("Opening keystore with url {}", url);

            return url.openStream();
        }
    }

    static public KeyStore loadKeyStore(URI uri, char[] password) throws Exception {
        try (InputStream stream = openKeyStoreInput(uri)) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(stream, password);
            return ks;
        }
    }

    static public SSLContext createSSLContext(
        URI keystoreUri, char[] keystorePassword,
        URI truststoreUri, char[] truststorePassword) throws Exception {

        // load keystore
        KeyStore keystore = loadKeyStore(keystoreUri, keystorePassword);
        KeyManager[] keyManagers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword);
        keyManagers = keyManagerFactory.getKeyManagers();

        // load truststore
        KeyStore truststore = loadKeyStore(truststoreUri, truststorePassword);
        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }

}
