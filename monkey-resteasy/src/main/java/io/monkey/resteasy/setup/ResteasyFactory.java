package io.monkey.resteasy.setup;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.util.Map;

import static io.undertow.servlet.Servlets.servlet;

public class ResteasyFactory {

    private final static String SERVLET_NAME = "ResteasyServlet";

    @JsonProperty
    private String contextPath;

    @JsonProperty
    private Map<String, String> contextParams;

    @JsonProperty
    private Map<String, String> initParams;

    public DeploymentInfo build(ResteasyDeployment deployment) {

        if (contextPath == null) contextPath = "/";
        if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;

        DeploymentInfo deploymentInfo = new DeploymentInfo();

        deploymentInfo.setContextPath(this.contextPath);
        deploymentInfo.setDeploymentName("Resteasy" + contextPath);
        deploymentInfo.setClassLoader(deployment.getClass().getClassLoader());

        if (contextParams != null) {
            for (Map.Entry<String, String> e : contextParams.entrySet()) {
                deploymentInfo.addInitParameter(e.getKey(), e.getValue());
            }
        }

        ServletInfo resteasyServlet = servlet(SERVLET_NAME, HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addMapping("/*");

        if (initParams != null) {
            for (Map.Entry<String, String> e : initParams.entrySet()) {
                resteasyServlet.addInitParam(e.getKey(), e.getValue());
            }
        }

        deploymentInfo.addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
                .addServlet(resteasyServlet);

        return deploymentInfo;

    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Map<String, String> getContextParams() {
        return contextParams;
    }

    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }
}
