package io.sunflower.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

import io.sunflower.Configuration;

/**
 * Created by michael on 17/9/2.
 */
public class ExampleConfiguration extends Configuration {

    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }
}
