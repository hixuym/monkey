package io.sunflower.server;

import com.google.common.base.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import io.sunflower.validation.ValidationMethod;
import io.undertow.Undertow;

/**
 * Builds HTTPS connectors (HTTP over TLS/SSL).
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>{@code keyStorePath}</td>
 * <td><b>REQUIRED</b></td>
 * <td>
 * The path to the Java key store which contains the host certificate and private key.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code keyStorePassword}</td>
 * <td><b>REQUIRED</b></td>
 * <td>
 * The password used to access the key store.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code keyStoreType}</td>
 * <td>{@code JKS}</td>
 * <td>
 * The type of key store (usually {@code JKS}, {@code PKCS12}, {@code JCEKS},
 * {@code Windows-MY}, or {@code Windows-ROOT}).
 * </td>
 * </tr>
 * <tr>
 * <td>{@code keyStoreProvider}</td>
 * <td>(none)</td>
 * <td>
 * The JCE provider to use to access the key store.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code trustStorePath}</td>
 * <td>(none)</td>
 * <td>
 * The path to the Java key store which contains the CA certificates used to establish
 * trust.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code trustStorePassword}</td>
 * <td>(none)</td>
 * <td>The password used to access the trust store.</td>
 * </tr>
 * <tr>
 * <td>{@code trustStoreType}</td>
 * <td>{@code JKS}</td>
 * <td>
 * The type of trust store (usually {@code JKS}, {@code PKCS12}, {@code JCEKS},
 * {@code Windows-MY}, or {@code Windows-ROOT}).
 * </td>
 * </tr>
 * <tr>
 * <td>{@code trustStoreProvider}</td>
 * <td>(none)</td>
 * <td>
 * The JCE provider to use to access the trust store.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code keyManagerPassword}</td>
 * <td>(none)</td>
 * <td>The password, if any, for the key manager.</td>
 * </tr>
 * <tr>
 * <td>{@code needClientAuth}</td>
 * <td>(none)</td>
 * <td>Whether or not client authentication is required.</td>
 * </tr>
 * <tr>
 * <td>{@code wantClientAuth}</td>
 * <td>(none)</td>
 * <td>Whether or not client authentication is requested.</td>
 * </tr>
 * <tr>
 * <td>{@code certAlias}</td>
 * <td>(none)</td>
 * <td>The alias of the certificate to use.</td>
 * </tr>
 * <tr>
 * <td>{@code crlPath}</td>
 * <td>(none)</td>
 * <td>The path to the file which contains the Certificate Revocation List.</td>
 * </tr>
 * <tr>
 * <td>{@code enableCRLDP}</td>
 * <td>false</td>
 * <td>Whether or not CRL Distribution Points (CRLDP) support is enabled.</td>
 * </tr>
 * <tr>
 * <td>{@code enableOCSP}</td>
 * <td>false</td>
 * <td>Whether or not On-Line Certificate Status Protocol (OCSP) support is enabled.</td>
 * </tr>
 * <tr>
 * <td>{@code maxCertPathLength}</td>
 * <td>(unlimited)</td>
 * <td>The maximum certification path length.</td>
 * </tr>
 * <tr>
 * <td>{@code ocspResponderUrl}</td>
 * <td>(none)</td>
 * <td>The location of the OCSP responder.</td>
 * </tr>
 * <tr>
 * <td>{@code jceProvider}</td>
 * <td>(none)</td>
 * <td>The name of the JCE provider to use for cryptographic support.</td>
 * </tr>
 * <tr>
 * <td>{@code validateCerts}</td>
 * <td>false</td>
 * <td>
 * Whether or not to validate TLS certificates before starting. If enabled, Dropwizard
 * will refuse to start with expired or otherwise invalid certificates. This option will
 * cause unconditional failured in Dropwizard 1.x until a new validation mechanism can be
 * implemented.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code validatePeers}</td>
 * <td>false</td>
 * <td>
 * Whether or not to validate TLS peer certificates. This option will
 * cause unconditional failured in Dropwizard 1.x until a new validation mechanism can be
 * implemented.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code supportedProtocols}</td>
 * <td>JVM default</td>
 * <td>
 * A list of protocols (e.g., {@code SSLv3}, {@code TLSv1}) which are supported. All
 * other protocols will be refused.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code excludedProtocols}</td>
 * <td>Jetty's default</td>
 * <td>
 * A list of protocols (e.g., {@code SSLv3}, {@code TLSv1}) which are excluded. These
 * protocols will be refused.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code supportedCipherSuites}</td>
 * <td>JVM default</td>
 * <td>
 * A list of cipher suites (e.g., {@code TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256}) which
 * are supported. All other cipher suites will be refused
 * </td>
 * </tr>
 * <tr>
 * <td>{@code excludedCipherSuites}</td>
 * <td>Jetty's default</td>
 * <td>
 * A list of cipher suites (e.g., {@code TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256}) which
 * are excluded. These cipher suites will be refused.
 * </td>
 * </tr>
 * <tr>
 * <td>{@code allowRenegotiation}</td>
 * <td>true</td>
 * <td>Whether or not TLS renegotiation is allowed.</td>
 * </tr>
 * <tr>
 * <td>{@code endpointIdentificationAlgorithm}</td>
 * <td>(none)</td>
 * <td>
 * Which endpoint identification algorithm, if any, to use during the TLS handshake.
 * </td>
 * </tr>
 * </table>
 * <p/>
 * For more configuration parameters, see {@link HttpListenerFactory}.
 *
 * @see HttpListenerFactory
 */
@JsonTypeName("https")
public class HttpsListenerFactory extends HttpListenerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsListenerFactory.class);
    private static final AtomicBoolean LOGGED = new AtomicBoolean(false);

    private String keyStorePath;

    private String keyStorePassword;

    @NotEmpty
    private String keyStoreType = "JKS";

    private String keyStoreProvider;

    private String trustStorePath;

    private String trustStorePassword;

    @NotEmpty
    private String trustStoreType = "JKS";

    private String trustStoreProvider;

    private String keyManagerPassword;

    private Boolean needClientAuth;
    private Boolean wantClientAuth;
    private String certAlias;
    private File crlPath;
    private Boolean enableCRLDP;
    private Boolean enableOCSP;
    private Integer maxCertPathLength;
    private URI ocspResponderUrl;
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
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @JsonProperty
    public void setKeyStorePath(String keyStorePath) {
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
    public String getTrustStorePath() {
        return trustStorePath;
    }

    @JsonProperty
    public void setTrustStorePath(String trustStorePath) {
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
    public File getCrlPath() {
        return crlPath;
    }

    @JsonProperty
    public void setCrlPath(File crlPath) {
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
    public URI getOcspResponderUrl() {
        return ocspResponderUrl;
    }

    @JsonProperty
    public void setOcspResponderUrl(URI ocspResponderUrl) {
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

    private SSLContext sslContext;

    private SSLContext createSslContext() {
        return null; //TODO
    }

    @Override
    public Undertow.ListenerBuilder build() {

        Undertow.ListenerBuilder builder = new Undertow.ListenerBuilder();

        builder.setPort(getPort());
        builder.setHost(getBindHost());
        builder.setType(Undertow.ListenerType.HTTPS);

        this.sslContext = createSslContext();

        logSupportedParameters(sslContext);

        builder.setSslContext(sslContext);

        return builder;
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

}
