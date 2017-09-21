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

import io.sunflower.gizmo.utils.Mode;
import io.sunflower.gizmo.utils.SecretGenerator;
import io.sunflower.undertow.ConnectorFactory;
import io.sunflower.undertow.HttpConnectorFactory;
import io.sunflower.util.Duration;

/**
 *
 */
public class GizmoConfiguration {

    private String applicationContextPath;
    private String adminContextPath;

    private String applicationSecret = SecretGenerator.generateSecret();
    private List<String> applicationLangs = Arrays.asList("zh", "en");


    private String cookieDomain = "sunflower.io";
    private String cookiePrefix = "SF_";
    private boolean cookieEncrypted = false;

    private Duration sessionExpireTime = Duration.hours(1);
    private boolean sessionSendOnlyIfChanged = true;
    private boolean sessionTransferredOverHttpsOnly = false;
    private boolean sessionHttpOnly = true;

    private String httpCacheMaxAge = "3600";
    private boolean etagEnable = true;

    private Map<String, String> mimetypes = new HashMap<>();

    private String uploadTempFolder;
    private String jsonpCallbackParam = "callback";
    private boolean diagnosticsEnabled = true;
    private boolean usageOfXForwardedHeaderEnabled = false;

    private Mode mode = Mode.prod;

    // undertow conf
    private boolean http2Enabled = false;
    private boolean traceEnabled = false;

    private String accessLogFormat;
    private boolean accessLogRotate = true;
    private String accessLogPath = "./logs";

    @Valid
    @NotNull
    private List<ConnectorFactory> applicationConnectors = Collections.singletonList(HttpConnectorFactory.application());

    @Valid
    @NotNull
    private List<ConnectorFactory> adminConnectors = Collections.singletonList(HttpConnectorFactory.admin());

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
    public boolean isEtagEnable() {
        return etagEnable;
    }

    @JsonProperty
    public void setEtagEnable(boolean etagEnable) {
        this.etagEnable = etagEnable;
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

    @JsonProperty
    public String getCookiePrefix() {
        return cookiePrefix;
    }

    @JsonProperty
    public void setCookiePrefix(String cookiePrefix) {
        this.cookiePrefix = cookiePrefix;
    }

    @JsonProperty
    public List<String> getApplicationLangs() {
        return applicationLangs;
    }

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(String applicationContextPath) {
        this.applicationContextPath = applicationContextPath;
    }

    @JsonProperty
    public String getAdminContextPath() {
        return adminContextPath;
    }

    @JsonProperty
    public void setAdminContextPath(String adminContextPath) {
        this.adminContextPath = adminContextPath;
    }

    @JsonProperty
    public boolean isUsageOfXForwardedHeaderEnabled() {
        return usageOfXForwardedHeaderEnabled;
    }

    @JsonProperty
    public void setUsageOfXForwardedHeaderEnabled(boolean usageOfXForwardedHeaderEnabled) {
        this.usageOfXForwardedHeaderEnabled = usageOfXForwardedHeaderEnabled;
    }

    @JsonProperty
    public void setApplicationLangs(List<String> applicationLangs) {
        this.applicationLangs = applicationLangs;
    }

    @JsonIgnore
    public boolean isProd() {
        return Mode.prod == this.mode;
    }

    @JsonIgnore
    public boolean isDev() { return this.mode == Mode.dev; }

    @JsonIgnore
    public boolean isTest() { return this.mode == Mode.test; }

    @JsonProperty
    public Mode getMode() {
        return mode;
    }

    @JsonProperty
    public void setMode(Mode mode) {
        this.mode = mode;
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

    @JsonProperty
    public boolean isHttp2Enabled() {
        return http2Enabled;
    }

    @JsonProperty
    public void setHttp2Enabled(boolean http2Enabled) {
        this.http2Enabled = http2Enabled;
    }

    @JsonProperty
    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    @JsonProperty
    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    @JsonProperty
    public String getAccessLogFormat() {
        return accessLogFormat;
    }

    @JsonProperty
    public void setAccessLogFormat(String accessLogFormat) {
        this.accessLogFormat = accessLogFormat;
    }

    @JsonProperty
    public String getAccessLogPath() {
        return accessLogPath;
    }

    @JsonProperty
    public void setAccessLogPath(String accessLogPath) {
        this.accessLogPath = accessLogPath;
    }

    public boolean isAccessLogRotate() {
        return accessLogRotate;
    }

    public void setAccessLogRotate(boolean accessLogRotate) {
        this.accessLogRotate = accessLogRotate;
    }
}
