package io.sunflower.gizmo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.sunflower.util.Duration;

/**
 *
 */
public class GizmoConfiguration {

    private String cookieSuffix = "SF_LANG";
    private String cookieDomain = "sunflower.io";
    private String cookiePrefix = "SF";
    private boolean cookieEncrypted = false;

    private String httpCacheMaxAge = "3600";

    @NotNull
    private String applicationSecret;
    private String applicationModulesBasePackage;
    private String applicationContextPath;
    private String adminContextPath;

    private List<String> applicationLangs = Arrays.asList("zh", "en");

    private Duration sessionExpireTime = Duration.hours(1);

    private boolean sessionSendOnlyIfChanged = true;
    private boolean sessionTransferredOverHttpsOnly = false;
    private boolean sessionHttpOnly = true;

    private boolean useETag = true;

    private String host = "127.0.0.1";

    private Integer port = 8080;
    private Integer sslPort = -1;

    private String sslKeystoreUri;
    private String sslKeystorePass;
    private String sslTruststoreUri;
    private String sslTruststorePass;
    private Map<String, String> mimetypes = new HashMap<>();
    private Duration idleTimeout;

    private boolean http2Enabled = false;
    private boolean traceEnabled = false;

    private String uploadTempFolder;
    private String jsonpCallbackParam = "callback";
    private boolean diagnosticsEnabled = true;
    private boolean usageOfXForwardedHeaderEnabled = false;

    @Valid
    @NotNull
    private List<ConnectorFactory> applicationConnectors = Collections.singletonList(HttpConnectorFactory.application());

    @Valid
    @NotNull
    private List<ConnectorFactory> adminConnectors = Collections.singletonList(HttpConnectorFactory.admin());

    @JsonProperty
    public String getCookieSuffix() {
        return cookieSuffix;
    }

    @JsonProperty
    public void setCookieSuffix(String cookieSuffix) {
        this.cookieSuffix = cookieSuffix;
    }

    @JsonProperty
    public String getCookieDomain() {
        return cookieDomain;
    }

    @JsonProperty
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    @JsonProperty
    public boolean isCookieEncrypted() {
        return cookieEncrypted;
    }

    @JsonProperty
    public void setCookieEncrypted(boolean cookieEncrypted) {
        this.cookieEncrypted = cookieEncrypted;
    }

    @JsonProperty
    public String getHttpCacheMaxAge() {
        return httpCacheMaxAge;
    }

    @JsonProperty
    public void setHttpCacheMaxAge(String httpCacheMaxAge) {
        this.httpCacheMaxAge = httpCacheMaxAge;
    }

    @JsonProperty
    public String getApplicationSecret() {
        return applicationSecret;
    }

    @JsonProperty
    public void setApplicationSecret(String applicationSecret) {
        this.applicationSecret = applicationSecret;
    }

    @JsonProperty
    public Duration getSessionExpireTime() {
        return sessionExpireTime;
    }

    @JsonProperty
    public void setSessionExpireTime(Duration sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }

    @JsonProperty
    public boolean isSessionSendOnlyIfChanged() {
        return sessionSendOnlyIfChanged;
    }

    @JsonProperty
    public void setSessionSendOnlyIfChanged(boolean sessionSendOnlyIfChanged) {
        this.sessionSendOnlyIfChanged = sessionSendOnlyIfChanged;
    }

    @JsonProperty
    public boolean isSessionTransferredOverHttpsOnly() {
        return sessionTransferredOverHttpsOnly;
    }

    @JsonProperty
    public void setSessionTransferredOverHttpsOnly(boolean sessionTransferredOverHttpsOnly) {
        this.sessionTransferredOverHttpsOnly = sessionTransferredOverHttpsOnly;
    }

    @JsonProperty
    public boolean isSessionHttpOnly() {
        return sessionHttpOnly;
    }

    @JsonProperty
    public void setSessionHttpOnly(boolean sessionHttpOnly) {
        this.sessionHttpOnly = sessionHttpOnly;
    }

    @JsonProperty
    public boolean isUseETag() {
        return useETag;
    }

    @JsonProperty
    public void setUseETag(boolean useETag) {
        this.useETag = useETag;
    }

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public Integer getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(Integer port) {
        this.port = port;
    }

    @JsonProperty
    public Integer getSslPort() {
        return sslPort;
    }

    @JsonProperty
    public void setSslPort(Integer sslPort) {
        this.sslPort = sslPort;
    }

    @JsonProperty
    public String getSslKeystoreUri() {
        return sslKeystoreUri;
    }

    @JsonProperty
    public void setSslKeystoreUri(String sslKeystoreUri) {
        this.sslKeystoreUri = sslKeystoreUri;
    }

    @JsonProperty
    public String getSslKeystorePass() {
        return sslKeystorePass;
    }

    @JsonProperty
    public void setSslKeystorePass(String sslKeystorePass) {
        this.sslKeystorePass = sslKeystorePass;
    }

    @JsonProperty
    public String getSslTruststoreUri() {
        return sslTruststoreUri;
    }

    @JsonProperty
    public void setSslTruststoreUri(String sslTruststoreUri) {
        this.sslTruststoreUri = sslTruststoreUri;
    }

    @JsonProperty
    public String getSslTruststorePass() {
        return sslTruststorePass;
    }

    @JsonProperty
    public void setSslTruststorePass(String sslTruststorePass) {
        this.sslTruststorePass = sslTruststorePass;
    }

    @JsonProperty
    public Map<String, String> getMimetypes() {
        return mimetypes;
    }

    @JsonProperty
    public void setMimetypes(Map<String, String> mimetypes) {
        this.mimetypes = mimetypes;
    }

    @JsonProperty
    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    @JsonProperty
    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @JsonProperty
    public String getUploadTempFolder() {
        return uploadTempFolder;
    }

    @JsonProperty
    public void setUploadTempFolder(String uploadTempFolder) {
        this.uploadTempFolder = uploadTempFolder;
    }

    @JsonProperty
    public String getJsonpCallbackParam() {
        return jsonpCallbackParam;
    }

    @JsonProperty
    public void setJsonpCallbackParam(String jsonpCallbackParam) {
        this.jsonpCallbackParam = jsonpCallbackParam;
    }

    @JsonProperty
    public boolean isDiagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    @JsonProperty
    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) {
        this.diagnosticsEnabled = diagnosticsEnabled;
    }

    @JsonProperty
    public List<ConnectorFactory> getApplicationConnectors() {
        return applicationConnectors;
    }

    public String getCookiePrefix() {
        return cookiePrefix;
    }

    public void setCookiePrefix(String cookiePrefix) {
        this.cookiePrefix = cookiePrefix;
    }

    public List<String> getApplicationLangs() {
        return applicationLangs;
    }

    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    public void setApplicationContextPath(String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    public String getAdminContextPath() {
        return adminContextPath;
    }

    public void setAdminContextPath(String adminContextPath) {
        this.adminContextPath = adminContextPath;
    }

    public boolean isUsageOfXForwardedHeaderEnabled() {
        return usageOfXForwardedHeaderEnabled;
    }

    public void setUsageOfXForwardedHeaderEnabled(boolean usageOfXForwardedHeaderEnabled) {
        this.usageOfXForwardedHeaderEnabled = usageOfXForwardedHeaderEnabled;
    }

    public String getApplicationModulesBasePackage() {
        return applicationModulesBasePackage;
    }

    public void setApplicationModulesBasePackage(String applicationModulesBasePackage) {
        this.applicationModulesBasePackage = applicationModulesBasePackage;
    }

    public void setApplicationLangs(List<String> applicationLangs) {
        this.applicationLangs = applicationLangs;
    }

    @JsonIgnore
    public boolean isProd() {
        return true;
    }
    @JsonIgnore
    public boolean isDev() { return false; }
    @JsonIgnore
    public boolean isTest() { return false; }

    @JsonIgnore
    public boolean isPortEnabled() {
        return this.port != null && this.port > 0;
    }

    @JsonIgnore
    public boolean isSslPortEnabled() {
        return this.sslPort != null && this.sslPort > 0;
    }

    @JsonProperty
    public void setApplicationConnectors(List<ConnectorFactory> applicationConnectors) {
        this.applicationConnectors = applicationConnectors;
    }

    @JsonProperty
    public List<ConnectorFactory> getAdminConnectors() {
        return adminConnectors;
    }

    @JsonProperty
    public void setAdminConnectors(List<ConnectorFactory> adminConnectors) {
        this.adminConnectors = adminConnectors;
    }

    public boolean isHttp2Enabled() {
        return http2Enabled;
    }

    public void setHttp2Enabled(boolean http2Enabled) {
        this.http2Enabled = http2Enabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    @JsonIgnore
    public String getLoggableIdentifier() {
        // build list of ports
        StringBuilder ports = new StringBuilder();

        if (isPortEnabled()) {
            ports.append(getPort());
        }

        if (isSslPortEnabled()) {
            if (ports.length() > 0) {
                ports.append(", ");
            }
            ports.append(getSslPort());
            ports.append("/ssl");
        }

        StringBuilder s = new StringBuilder();

        s.append("on ");

        s.append(Optional.ofNullable(getHost()).orElse("<all>"));
        s.append(":");
        s.append(ports);

        return s.toString();
    }
}
