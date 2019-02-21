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

package io.monkey.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>A {@link X509ExtendedKeyManager} that selects a key with an alias
 * retrieved from SNI information, delegating other processing to a nested X509ExtendedKeyManager.</p>
 * <p>Can only be used on server side.</p>
 */
public class SniX509ExtendedKeyManager extends X509ExtendedKeyManager {
    public static final String SNI_X509 = "org.eclipse.jetty.util.ssl.snix509";
    private static final String NO_MATCHERS = "no_matchers";
    private static final Logger LOG = LoggerFactory.getLogger(SniX509ExtendedKeyManager.class);

    private final X509ExtendedKeyManager _delegate;

    public SniX509ExtendedKeyManager(X509ExtendedKeyManager keyManager) {
        _delegate = keyManager;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return _delegate.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return _delegate.chooseEngineClientAlias(keyType, issuers, engine);
    }

    protected String chooseServerAlias(String keyType, Principal[] issuers, Collection<SNIMatcher> matchers, SSLSession session) {
        // Look for the aliases that are suitable for the keytype and issuers
        String[] aliases = _delegate.getServerAliases(keyType, issuers);
        if (aliases == null || aliases.length == 0)
            return null;

        // Look for the SNI information.
        String host = null;
        X509 x509 = null;
        if (matchers != null) {
            for (SNIMatcher m : matchers) {
                if (m instanceof SslContextFactory.AliasSNIMatcher) {
                    SslContextFactory.AliasSNIMatcher matcher = (SslContextFactory.AliasSNIMatcher) m;
                    host = matcher.getHost();
                    x509 = matcher.getX509();
                    break;
                }
            }
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Matched {} with {} from {}", host, x509, Arrays.asList(aliases));

        // Check if the SNI selected alias is allowable
        if (x509 != null) {
            for (String a : aliases) {
                if (a.equals(x509.getAlias())) {
                    session.putValue(SNI_X509, x509);
                    return a;
                }
            }
            return null;
        }
        return NO_MATCHERS;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        SSLSocket sslSocket = (SSLSocket) socket;
        String alias = socket == null ? NO_MATCHERS : chooseServerAlias(keyType, issuers, sslSocket.getSSLParameters().getSNIMatchers(), sslSocket.getHandshakeSession());
        if (alias == NO_MATCHERS)
            alias = _delegate.chooseServerAlias(keyType, issuers, socket);
        if (LOG.isDebugEnabled())
            LOG.debug("Chose alias {}/{} on {}", alias, keyType, socket);
        return alias;
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        String alias = engine == null ? NO_MATCHERS : chooseServerAlias(keyType, issuers, engine.getSSLParameters().getSNIMatchers(), engine.getHandshakeSession());
        if (alias == NO_MATCHERS)
            alias = _delegate.chooseEngineServerAlias(keyType, issuers, engine);
        if (LOG.isDebugEnabled())
            LOG.debug("Chose alias {}/{} on {}", alias, keyType, engine);
        return alias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return _delegate.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return _delegate.getClientAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return _delegate.getPrivateKey(alias);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return _delegate.getServerAliases(keyType, issuers);
    }
}