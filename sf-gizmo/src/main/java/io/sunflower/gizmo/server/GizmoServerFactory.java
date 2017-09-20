package io.sunflower.gizmo.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

import io.sunflower.gizmo.utils.NinjaMode;
import io.sunflower.server.Server;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Environment;
import io.sunflower.util.Duration;

/**
 * Created by michael on 17/9/12.
 */
public class GizmoServerFactory implements ServerFactory, GizmoConfiguration {

    @NotNull
    private NinjaMode mode = NinjaMode.dev;

    @Override @JsonProperty
    public NinjaMode getMode() {
        return null;
    }

    @JsonProperty
    public void setMode(NinjaMode mode) {
        this.mode = mode;
    }

    @Override
    public Server build(Environment environment) {
        return null;
    }

    @Override
    public void configure(Environment environment) {
        environment.bind(GizmoConfiguration.class, this);
    }

    @Override
    public String getApplicationContextPath() {
        return null;
    }

    @Override
    public String getAdminContextPath() {
        return null;
    }

    @Override
    public String getApplicationLanguages() {
        return null;
    }

    @Override
    public String getCookiePrefix() {
        return null;
    }

    @Override
    public String getCookieDomain() {
        return null;
    }

    @Override
    public String getApplicationSecret() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public Integer getPort() {
        return null;
    }

    @Override
    public Integer getSslPort() {
        return null;
    }

    @Override
    public Duration getIdleTimeout() {
        return null;
    }

    @Override
    public String getSslKeystoreUri() {
        return null;
    }

    @Override
    public String getSslKeystorePass() {
        return null;
    }

    @Override
    public String getSslTruststoreUri() {
        return null;
    }

    @Override
    public String getSslTruststorePass() {
        return null;
    }
}
