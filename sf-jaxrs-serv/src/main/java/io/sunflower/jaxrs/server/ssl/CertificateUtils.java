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

package io.sunflower.jaxrs.server.ssl;

import io.sunflower.util.Uris;

import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CertificateFactory;
import java.util.Collection;

/**
 * @author michael
 */
public class CertificateUtils {

    /* ------------------------------------------------------------ */
    public static KeyStore getKeyStore(URI store, String storeType, String storeProvider,
                                       String storePassword) throws Exception {
        KeyStore keystore = null;

        if (store != null) {
            if (storeProvider != null) {
                keystore = KeyStore.getInstance(storeType, storeProvider);
            } else {
                keystore = KeyStore.getInstance(storeType);
            }

            try (InputStream inStream = Uris.openStream(store)) {
                keystore.load(inStream, storePassword == null ? null : storePassword.toCharArray());
            }
        }

        return keystore;
    }

    /* ------------------------------------------------------------ */
    public static Collection<? extends CRL> loadCRL(URI crlPath) throws Exception {
        Collection<? extends CRL> crlList = null;

        if (crlPath != null) {
            InputStream in = null;
            try {
                in = Uris.openStream(crlPath);
                crlList = CertificateFactory.getInstance("X.509").generateCRLs(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }

        return crlList;
    }

}
