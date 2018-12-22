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

package io.sunflower.jaxrs.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.sunflower.jaxrs.server.netty.NettyJaxrsServer;
import io.sunflower.jaxrs.server.ssl.SslContextFactory;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.server.Server;
import io.sunflower.setup.Environment;
import io.sunflower.validation.ValidationMethod;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HttpsServerFactory
 *
 * @author michael
 * created on 17/11/10 21:15
 */
@JsonTypeName("https")
public class HttpsServerFactory extends JaxrsServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsServerFactory.class);
    private static final AtomicBoolean LOGGED = new AtomicBoolean(false);

    private URI keyStorePath = URI.create("classpath:/io/sunflower/jaxrs/server/sf.keystore");
    private URI trustStorePath = URI.create("classpath:/io/sunflower/jaxrs/server/sf.keystore");

    private String keyStorePassword = "123456";

    @NotEmpty
    private String keyStoreType = "JKS";

    private String keyStoreProvider;

    private String trustStorePassword = "123456";

    @NotEmpty
    private String trustStoreType = "JKS";

    private String trustStoreProvider;

    private String keyManagerPassword;

    private Boolean needClientAuth;
    private Boolean wantClientAuth;
    private String certAlias;
    private URI crlPath;
    private Boolean enableCRLDP;
    private Boolean enableOCSP;
    private Integer maxCertPathLength;
    private String ocspResponderUrl;
    private String jceProvider;
    private boolean validateCerts = false;
    private boolean validatePeers = false;
    private List<String> supportedProtocols;
    private List<String> excludedProtocols;
    private List<String> supportedCipherSuites;
    private List<String> excludedCipherSuites;
    private boolean allowRenegotiation = true;
    private String endpointIdentificationAlgorithm;

    @JsonProperty
    public boolean getAllowRenegotiation() {
        return allowRenegotiation;
    }

    @JsonProperty
    public void setAllowRenegotiation(boolean allowRenegotiation) {
        this.allowRenegotiation = allowRenegotiation;
    }

    @JsonProperty
    public String getEndpointIdentificationAlgorithm() {
        return endpointIdentificationAlgorithm;
    }

    @JsonProperty
    public void setEndpointIdentificationAlgorithm(String endpointIdentificationAlgorithm) {
        this.endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
    }

    @JsonProperty
    public URI getKeyStorePath() {
        return keyStorePath;
    }

    @JsonProperty
    public void setKeyStorePath(URI keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    @JsonProperty
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    @JsonProperty
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @JsonProperty
    public String getKeyStoreType() {
        return keyStoreType;
    }

    @JsonProperty
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    @JsonProperty
    public String getKeyStoreProvider() {
        return keyStoreProvider;
    }

    @JsonProperty
    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    @JsonProperty
    public String getTrustStoreType() {
        return trustStoreType;
    }

    @JsonProperty
    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    @JsonProperty
    public String getTrustStoreProvider() {
        return trustStoreProvider;
    }

    @JsonProperty
    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    @JsonProperty
    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    @JsonProperty
    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

    @JsonProperty
    public URI getTrustStorePath() {
        return trustStorePath;
    }

    @JsonProperty
    public void setTrustStorePath(URI trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    @JsonProperty
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    @JsonProperty
    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    @JsonProperty
    public Boolean getNeedClientAuth() {
        return needClientAuth;
    }

    @JsonProperty
    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    @JsonProperty
    public Boolean getWantClientAuth() {
        return wantClientAuth;
    }

    @JsonProperty
    public void setWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }

    @JsonProperty
    public String getCertAlias() {
        return certAlias;
    }

    @JsonProperty
    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    @JsonProperty
    public URI getCrlPath() {
        return crlPath;
    }

    @JsonProperty
    public void setCrlPath(URI crlPath) {
        this.crlPath = crlPath;
    }

    @JsonProperty
    public Boolean getEnableCRLDP() {
        return enableCRLDP;
    }

    @JsonProperty
    public void setEnableCRLDP(Boolean enableCRLDP) {
        this.enableCRLDP = enableCRLDP;
    }

    @JsonProperty
    public Boolean getEnableOCSP() {
        return enableOCSP;
    }

    @JsonProperty
    public void setEnableOCSP(Boolean enableOCSP) {
        this.enableOCSP = enableOCSP;
    }

    @JsonProperty
    public Integer getMaxCertPathLength() {
        return maxCertPathLength;
    }

    @JsonProperty
    public void setMaxCertPathLength(Integer maxCertPathLength) {
        this.maxCertPathLength = maxCertPathLength;
    }

    @JsonProperty
    public String getOcspResponderUrl() {
        return ocspResponderUrl;
    }

    @JsonProperty
    public void setOcspResponderUrl(String ocspResponderUrl) {
        this.ocspResponderUrl = ocspResponderUrl;
    }

    @JsonProperty
    public String getJceProvider() {
        return jceProvider;
    }

    @JsonProperty
    public void setJceProvider(String jceProvider) {
        this.jceProvider = jceProvider;
    }

    @JsonProperty
    public boolean getValidatePeers() {
        return validatePeers;
    }

    @JsonProperty
    public void setValidatePeers(boolean validatePeers) {
        this.validatePeers = validatePeers;
    }

    @JsonProperty
    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    @JsonProperty
    public void setSupportedProtocols(List<String> supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    @JsonProperty
    public List<String> getExcludedProtocols() {
        return excludedProtocols;
    }

    @JsonProperty
    public void setExcludedProtocols(List<String> excludedProtocols) {
        this.excludedProtocols = excludedProtocols;
    }

    @JsonProperty
    public List<String> getSupportedCipherSuites() {
        return supportedCipherSuites;
    }

    @JsonProperty
    public List<String> getExcludedCipherSuites() {
        return excludedCipherSuites;
    }

    @JsonProperty
    public void setExcludedCipherSuites(List<String> excludedCipherSuites) {
        this.excludedCipherSuites = excludedCipherSuites;
    }

    @JsonProperty
    public void setSupportedCipherSuites(List<String> supportedCipherSuites) {
        this.supportedCipherSuites = supportedCipherSuites;
    }

    @JsonProperty
    public boolean isValidateCerts() {
        return validateCerts;
    }

    @JsonProperty
    public void setValidateCerts(boolean validateCerts) {
        this.validateCerts = validateCerts;
    }

    @ValidationMethod(message = "keyStorePath should not be null")
    public boolean isValidKeyStorePath() {
        return keyStoreType.startsWith("Windows-") || keyStorePath != null;
    }

    @ValidationMethod(message = "keyStorePassword should not be null or empty")
    public boolean isValidKeyStorePassword() {
        return keyStoreType.startsWith("Windows-") ||
                !Strings.isNullOrEmpty(keyStorePassword);
    }

//    static {
//        Security.addProvider(new OpenSSLProvider());
//    }

    @Override
    protected Server buildServer(NettyJaxrsServer server, Environment environment) {

//        final SslContextFactory sslContextFactory = new SslContextFactory();

        try {
//            sslContextFactory.reload(this::configureSslContextFactory);

            List<String> ciphers = Lists.newArrayList("ECDHE-RSA-AES128-SHA",
                    "ECDHE-RSA-AES256-SHA", "AES128-SHA", "AES256-SHA", "DES-CBC3-SHA");

            SelfSignedCertificate ssc = new SelfSignedCertificate();
            SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).ciphers(ciphers).build();
            server.setSSLContext(sslCtx);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


//        final SSLContext sslContext = sslContextFactory.getSslContext();

//        environment.lifecycle().addLifeCycleListener(logSslInfoOnStart(sslContext));

        JaxrsServer jaxrsServer = new JaxrsServer(server, environment);

        jaxrsServer.setApplicationContextPath(getApplicationContextPath());
        jaxrsServer.setSchema(JaxrsServer.HTTPS);

        return jaxrsServer;
    }

    /**
     * Register a listener that waits until the ssl context factory has started. Once it has started
     * we can grab the fully initialized context so we can log the parameters.
     */
    protected AbstractLifeCycle.AbstractLifeCycleListener logSslInfoOnStart(final SSLContext sslContext) {
        return new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarted(LifeCycle event) {
                logSupportedParameters(sslContext);
            }
        };
    }

    private void logSupportedParameters(SSLContext context) {
        if (LOGGED.compareAndSet(false, true)) {
            final String[] protocols = context.getSupportedSSLParameters().getProtocols();
            final SSLSocketFactory factory = context.getSocketFactory();
            final String[] cipherSuites = factory.getSupportedCipherSuites();
            LOGGER.info("Supported protocols: {}", Arrays.toString(protocols));
            LOGGER.info("Supported cipher suites: {}", Arrays.toString(cipherSuites));

            if (getSupportedProtocols() != null) {
                LOGGER.info("Configured protocols: {}", getSupportedProtocols());
            }

            if (getExcludedProtocols() != null) {
                LOGGER.info("Excluded protocols: {}", getExcludedProtocols());
            }

            if (getSupportedCipherSuites() != null) {
                LOGGER.info("Configured cipher suites: {}", getSupportedCipherSuites());
            }

            if (getExcludedCipherSuites() != null) {
                LOGGER.info("Excluded cipher suites: {}", getExcludedCipherSuites());
            }
        }
    }

    protected SslContextFactory configureSslContextFactory(SslContextFactory factory) {
        if (keyStorePath != null) {
            factory.setKeyStorePath(keyStorePath);
        }

        final String keyStoreType = getKeyStoreType();
        if (keyStoreType.startsWith("Windows-")) {
            try {
                final KeyStore keyStore = KeyStore.getInstance(keyStoreType);

                keyStore.load(null, null);
                factory.setKeyStore(keyStore);
            } catch (Exception e) {
                throw new IllegalStateException("Windows key store not supported", e);
            }
        } else {
            factory.setKeyStoreType(keyStoreType);
            factory.setKeyStorePassword(keyStorePassword);
        }

        if (keyStoreProvider != null) {
            factory.setKeyStoreProvider(keyStoreProvider);
        }

        final String trustStoreType = getTrustStoreType();
        if (trustStoreType.startsWith("Windows-")) {
            try {
                final KeyStore keyStore = KeyStore.getInstance(trustStoreType);
                keyStore.load(null, null);
                factory.setTrustStore(keyStore);
            } catch (Exception e) {
                throw new IllegalStateException("Windows key store not supported", e);
            }
        } else {
            if (trustStorePath != null) {
                factory.setTrustStorePath(trustStorePath);
            }
            if (trustStorePassword != null) {
                factory.setTrustStorePassword(trustStorePassword);
            }
            factory.setTrustStoreType(trustStoreType);
        }

        if (trustStoreProvider != null) {
            factory.setTrustStoreProvider(trustStoreProvider);
        }

        if (keyManagerPassword != null) {
            factory.setKeyManagerPassword(keyManagerPassword);
        }

        if (needClientAuth != null) {
            factory.setNeedClientAuth(needClientAuth);
        }

        if (wantClientAuth != null) {
            factory.setWantClientAuth(wantClientAuth);
        }

        if (certAlias != null) {
            factory.setCertAlias(certAlias);
        }

        if (crlPath != null) {
            factory.setCrlPath(crlPath);
        }

        if (enableCRLDP != null) {
            factory.setEnableCRLDP(enableCRLDP);
        }

        if (enableOCSP != null) {
            factory.setEnableOCSP(enableOCSP);
        }

        if (maxCertPathLength != null) {
            factory.setMaxCertPathLength(maxCertPathLength);
        }

        if (ocspResponderUrl != null) {
            factory.setOcspResponderURL(ocspResponderUrl);
        }

        if (jceProvider != null) {
            factory.setProvider(jceProvider);
        }

        factory.setRenegotiationAllowed(allowRenegotiation);
        factory.setEndpointIdentificationAlgorithm(endpointIdentificationAlgorithm);

        factory.setValidateCerts(validateCerts);
        factory.setValidatePeerCerts(validatePeers);

        if (supportedProtocols != null) {
            factory.setIncludeProtocols(Iterables.toArray(supportedProtocols, String.class));
        }

        if (excludedProtocols != null) {
            factory.setExcludeProtocols(Iterables.toArray(excludedProtocols, String.class));
        }

        if (supportedCipherSuites != null) {
            factory.setIncludeCipherSuites(Iterables.toArray(supportedCipherSuites, String.class));
        }

        if (excludedCipherSuites != null) {
            factory.setExcludeCipherSuites(Iterables.toArray(excludedCipherSuites, String.class));
        }

        return factory;
    }

}
