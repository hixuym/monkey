package io.sunflower.gizmo.server;

import java.util.Map;

import io.sunflower.gizmo.utils.NinjaMode;
import io.sunflower.util.Duration;

/**
 * Created by michael on 17/9/12.
 */
public interface GizmoConfiguration {

    // /////////////////////////////////////////////////
    // The 3 basic modes for ninja.
    // they should be set as system property: -Dninja.mode=test
    // and so on
    String MODE_KEY_NAME = "gizmo.mode";
    // and the values for the modes:
    String MODE_TEST = "test";
    String MODE_DEV = "dev";
    String MODE_PROD = "prod";

    // /////////////////////////////////////////////////
    // The basic directories used in all convention
    // over configuration operations:
    String VIEWS_DIR = "views";
    String CONTROLLERS_DIR = "controllers";
    String MODELS_DIR = "models";

    // location of the default views for errors:
    String LOCATION_VIEW_FTL_HTML_NOT_FOUND = "views/system/404notFound.ftl.html";
    String LOCATION_VIEW_FTL_HTML_BAD_REQUEST = "views/system/400badRequest.ftl.html";
    String LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR = "views/system/500internalServerError.ftl.html";
    String LOCATION_VIEW_FTL_HTML_UNAUTHORIZED = "views/system/401unauthorized.ftl.html";
    String LOCATION_VIEW_FTL_HTML_FORBIDDEN = "views/system/403forbidden.ftl.html";

    // i18n keys and default messages of Ninja
    // create the keys in your own messages.properties file to customize the message
    String I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY = "ninja.system.bad_request.text";
    String I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT = "Oops. That''s a bad request and all we know.";

    String I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY = "ninja.system.internal_server_error.text";
    String I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT = "Oops. That''s an internal server error and all we know.";

    String I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY = "ninja.system.not_found.text";
    String I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT = "Oops. The requested route cannot be found.";

    String I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_KEY = "ninja.system.unauthorized.text";
    String I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_DEFAULT = "Oops. You are unauthorized.";

    String I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY = "ninja.system.forbidden.text";
    String I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT = "Oops. That''s forbidden and all we know.";

    /** Used as spacer for instance in session cookie */
    String UNI_CODE_NULL_ENTITY = "\u0000";

    /** yea. utf-8 */
    String UTF_8 = "utf-8";

    String getApplicationName();

    String getApplicationContextPath();

    String getAdminContextPath();

    String getApplicationLanguages();

    default String getCookieSuffix() {
        return "_LANG";
    }

    String getCookiePrefix();
    String getCookieDomain();

    default String getHttpCacheMaxAge() {
        return "3600";
    }

    default boolean isCookieEncrypted() {
        return false;
    }

    String getApplicationSecret();

    default Duration getSessionExpireTime() {
        return Duration.hours(1);
    }

    default boolean isSeesionSendOnlyIfChanged() {
        return true;
    }

    default boolean isSessionTransferredOverHttpsOnly() {
        return false;
    }

    default boolean isSessionHttpOnly() {
        return true;
    }

    default boolean isDiagnosticsEnabled() {
        return NinjaMode.dev == getMode();
    }

    default String getSessionSuffix() {
        return "_SESSION";
    }

    default String getFlashSuffix() {
        return "_FLASH";
    }

    default boolean isUsageOfXForwardedHeaderEnabled() {
        return false;
    }

    default boolean useEtag() {
        return true;
    }

    String getHost();

    Integer getPort();
    Integer getSslPort();

    default boolean isPortEnabled() {
        return getPort() != null && this.getPort() > 0;
    }

    default boolean isSslPortEnabled() {
        return getSslPort() != null && this.getSslPort() > 0;
    }

    String getApplicationModulesBasePackage();

    Duration getIdleTimeout();
    String getSslKeystoreUri();
    String getSslKeystorePass();
    String getSslTruststoreUri();
    String getSslTruststorePass();

    NinjaMode getMode();

    default boolean isProd() {
        return getMode() == NinjaMode.prod;
    }

    default boolean isDev() {
        return getMode() == NinjaMode.dev;
    }

    default boolean isTest() {
        return getMode() == NinjaMode.test;
    }

    Map<String, String> getMimetypes();

    String getUploadTempFolder() ;

    default String getFreemarkerFileSuffix() {
        return ".ftl.html";
    }

    default String getJsonpCallbackParam() {
        return "callback";
    }

    default String getServerErrorTemplate() {
        return LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR;
    }

    default String getHtmlNotFoundTemplate() {
        return LOCATION_VIEW_FTL_HTML_NOT_FOUND;
    }

    default String getBadRequestTemplate() {
        return LOCATION_VIEW_FTL_HTML_BAD_REQUEST;
    }

    default String getForbiddenTemplate() {
        return LOCATION_VIEW_FTL_HTML_FORBIDDEN;
    }

    default String getUnauthorizedTemplate() {
        return LOCATION_VIEW_FTL_HTML_UNAUTHORIZED;
    }
}
