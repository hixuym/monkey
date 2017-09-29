package io.sunflower.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

import io.sunflower.Configuration;
import io.sunflower.db.DataSourceFactory;

/**
 * Created by michael on 17/9/2.
 */
public class ExampleConfiguration extends Configuration {

    @Valid
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

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

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
}
