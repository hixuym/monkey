package io.sunflower.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

import io.sunflower.Configuration;
import io.sunflower.gizmo.GizmoConfiguration;

/**
 * Created by michael on 17/9/2.
 */
public class ExampleConfiguration extends Configuration {

    @Valid
    private GizmoConfiguration gizmoConfiguration = new GizmoConfiguration();

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String name) {

        this.defaultName = name;
    }

    @JsonProperty("gizmo")
    public GizmoConfiguration getGizmoConfiguration() {
        return gizmoConfiguration;
    }

    @JsonProperty("gizmo")
    public void setGizmoConfiguration(GizmoConfiguration gizmoConfiguration) {
        this.gizmoConfiguration = gizmoConfiguration;
    }
}
