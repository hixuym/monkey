package io.sunflower.gizmo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Injector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import io.sunflower.gizmo.server.GizmoHttpHandler;
import io.sunflower.gizmo.utils.GizmoConstant;
import io.sunflower.gizmo.utils.Mode;
import io.sunflower.gizmo.utils.SecretGenerator;
import io.sunflower.lifecycle.AbstractLifeCycle;
import io.sunflower.lifecycle.LifeCycle;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.Task;
import io.sunflower.undertow.handler.TaskManager;
import io.sunflower.util.Duration;
import io.undertow.Handlers;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.form.FormParserFactory;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 *
 */
public class GizmoConfiguration {

    @JsonIgnore
    protected Logger logger = LoggerFactory.getLogger(getClass());

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

    private final TaskManager taskManager = new TaskManager();

    private String applicationContextPath;
    private String adminContextPath;

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
    public boolean isDev() {
        return this.mode == Mode.dev;
    }

    @JsonIgnore
    public boolean isTest() {
        return this.mode == Mode.test;
    }

    @JsonProperty
    public Mode getMode() {
        return mode;
    }

    @JsonProperty
    public void setMode(Mode mode) {
        this.mode = mode;
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

    @JsonProperty
    public boolean isAccessLogRotate() {
        return accessLogRotate;
    }

    @JsonProperty
    public void setAccessLogRotate(boolean accessLogRotate) {
        this.accessLogRotate = accessLogRotate;
    }

    @JsonIgnore
    public void addTask(Task task) {
        this.taskManager.add(task);
    }

    @JsonIgnore
    protected HttpHandler createAdminHandler(Environment environment) {

        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                logTasks();
            }
        });

        return new PathHandler()
            .addPrefixPath("tasks", TaskManager.createHandler(this.taskManager));

    }

    @JsonIgnore
    protected HttpHandler createApplicationHandler(Injector injector) {
        // root handler for ninja app
        GizmoHttpHandler gizmoHttpHandler = new GizmoHttpHandler();

        // slipstream injector into undertow handler BEFORE server starts
        gizmoHttpHandler.init(injector, getApplicationContextPath());

        HttpHandler h = gizmoHttpHandler;

        // wireshark enabled?
        if (isTraceEnabled()) {
            logger.info("Undertow tracing of requests and responses activated (undertow.tracing = true)");
            // only activate request dumping on non-assets
            Predicate isAssets = Predicates.prefix("/assets");
            h = Handlers.predicate(isAssets, h, new RequestDumpingHandler(h));
        }

        // then eagerly parse form data (which is then included as an attachment)
        FormParserFactory.Builder formParserFactoryBuilder = FormParserFactory.builder();
        formParserFactoryBuilder.setDefaultCharset(GizmoConstant.UTF_8);
        h = new EagerFormParsingHandler(formParserFactoryBuilder.build()).setNext(h);

        // then requests MUST be blocking for IO to function
        h = new BlockingHandler(h);

        return h;
    }

    @JsonIgnore
    protected HttpHandler addAccessLogWrapper(Environment environment, HttpHandler httpHandler) {
        String format = getAccessLogFormat();

        if (StringUtils.isNotEmpty(format)) {

            ExecutorService executorService = environment.lifecycle().executorService("AccessLog-pool-%d")
                .maxThreads(1)
                .minThreads(1)
                .threadFactory(new ThreadFactoryBuilder().setDaemon(true).build())
                .rejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
                .build();

            Path log = Paths.get(getAccessLogPath());

            if (!log.toFile().exists()) {
                log.toFile().mkdirs();
            }

            AccessLogReceiver receiver = DefaultAccessLogReceiver.builder()
                .setLogBaseName("access")
                .setLogWriteExecutor(executorService)
                .setOutputDirectory(log)
                .setRotate(isAccessLogRotate())
                .build();

            return new AccessLogHandler(httpHandler, receiver, format, environment.classLoader());
        }

        return httpHandler;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Gizmo.class);

    private void logTasks() {
        final StringBuilder stringBuilder = new StringBuilder(1024).append(String.format("%n%n"));

        for (Task task : this.taskManager.getTasks()) {
            final String taskClassName = firstNonNull(task.getClass().getCanonicalName(), task.getClass().getName());
            stringBuilder.append(String.format("    %-7s /tasks/%s (%s)%n",
                "POST",
                task.getName(),
                taskClassName));
        }

        LOGGER.info("tasks = {}", stringBuilder.toString());
    }
}
