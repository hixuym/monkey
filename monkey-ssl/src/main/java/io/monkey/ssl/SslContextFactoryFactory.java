package io.monkey.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import io.monkey.lifecycle.AbstractLifeCycle;
import io.monkey.lifecycle.LifeCycle;
import io.monkey.setup.Environment;
import io.monkey.validation.ValidationMethod;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SslContextFactoryFactory {

    private static Logger LOGGER = LoggerFactory.getLogger(SslContextFactoryFactory.class);

    @JsonProperty
    private String keyStorePath = "classpath:/io/monkey/ssl/monkey.keystore";
    @JsonProperty
    private String trustStorePath = "classpath:/io/monkey/ssl/monkey.truststore";

    @JsonProperty
    private String keyStorePassword = "monkey";
    @NotEmpty
    @JsonProperty
    private String keyStoreType = "JKS";
    @JsonProperty
    private String keyStoreProvider;
    @JsonProperty
    private String trustStorePassword = "monkey";
    @NotEmpty
    @JsonProperty
    private String trustStoreType = "JKS";
    @JsonProperty
    private String trustStoreProvider;
    @JsonProperty
    private String keyManagerPassword;
    @JsonProperty
    private Boolean needClientAuth;
    @JsonProperty
    private Boolean wantClientAuth;
    @JsonProperty
    private String certAlias;
    @JsonProperty
    private String crlPath;
    @JsonProperty
    private Boolean enableCRLDP;
    @JsonProperty
    private Boolean enableOCSP;
    @JsonProperty
    private Integer maxCertPathLength;
    @JsonProperty
    private String ocspResponderUrl;
    @JsonProperty
    private String jceProvider;
    @JsonProperty
    private boolean validateCerts = false;
    @JsonProperty
    private boolean validatePeers = false;
    @JsonProperty
    private List<String> supportedProtocols;
    @JsonProperty
    private List<String> excludedProtocols;
    @JsonProperty
    private List<String> supportedCipherSuites;
    @JsonProperty
    private List<String> excludedCipherSuites;
    @JsonProperty
    private boolean allowRenegotiation = true;
    @JsonProperty
    private String endpointIdentificationAlgorithm;

    public boolean getAllowRenegotiation() {
        return allowRenegotiation;
    }

    public void setAllowRenegotiation(boolean allowRenegotiation) {
        this.allowRenegotiation = allowRenegotiation;
    }

    public String getEndpointIdentificationAlgorithm() {
        return endpointIdentificationAlgorithm;
    }

    public void setEndpointIdentificationAlgorithm(String endpointIdentificationAlgorithm) {
        this.endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStoreProvider() {
        return keyStoreProvider;
    }

    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTrustStoreProvider() {
        return trustStoreProvider;
    }

    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public Boolean getNeedClientAuth() {
        return needClientAuth;
    }

    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public Boolean getWantClientAuth() {
        return wantClientAuth;
    }

    public void setWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    public String getCrlPath() {
        return crlPath;
    }

    public void setCrlPath(String crlPath) {
        this.crlPath = crlPath;
    }

    public Boolean getEnableCRLDP() {
        return enableCRLDP;
    }

    public void setEnableCRLDP(Boolean enableCRLDP) {
        this.enableCRLDP = enableCRLDP;
    }

    public Boolean getEnableOCSP() {
        return enableOCSP;
    }

    public void setEnableOCSP(Boolean enableOCSP) {
        this.enableOCSP = enableOCSP;
    }

    public Integer getMaxCertPathLength() {
        return maxCertPathLength;
    }

    public void setMaxCertPathLength(Integer maxCertPathLength) {
        this.maxCertPathLength = maxCertPathLength;
    }

    public String getOcspResponderUrl() {
        return ocspResponderUrl;
    }

    public void setOcspResponderUrl(String ocspResponderUrl) {
        this.ocspResponderUrl = ocspResponderUrl;
    }

    public String getJceProvider() {
        return jceProvider;
    }

    public void setJceProvider(String jceProvider) {
        this.jceProvider = jceProvider;
    }

    public boolean getValidatePeers() {
        return validatePeers;
    }

    public void setValidatePeers(boolean validatePeers) {
        this.validatePeers = validatePeers;
    }

    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    public void setSupportedProtocols(List<String> supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    public List<String> getExcludedProtocols() {
        return excludedProtocols;
    }

    public void setExcludedProtocols(List<String> excludedProtocols) {
        this.excludedProtocols = excludedProtocols;
    }

    public List<String> getSupportedCipherSuites() {
        return supportedCipherSuites;
    }

    public List<String> getExcludedCipherSuites() {
        return excludedCipherSuites;
    }

    public void setExcludedCipherSuites(List<String> excludedCipherSuites) {
        this.excludedCipherSuites = excludedCipherSuites;
    }

    public void setSupportedCipherSuites(List<String> supportedCipherSuites) {
        this.supportedCipherSuites = supportedCipherSuites;
    }

    public boolean isValidateCerts() {
        return validateCerts;
    }

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

    public SSLContext build(Environment environment) {
        final SslContextFactory sslContextFactory = new SslContextFactory();

        try {
            sslContextFactory.reload(this::configureSslContextFactory);
            sslContextFactory.secureConfigurationCheck();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final SSLContext sslContext = sslContextFactory.getSslContext();

        environment.lifecycle().addLifeCycleListener(logSslInfoOnStart(sslContext));

        return sslContext;
    }

    private static final AtomicBoolean LOGGED = new AtomicBoolean(false);

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

    SslContextFactory configureSslContextFactory(SslContextFactory factory) {
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
