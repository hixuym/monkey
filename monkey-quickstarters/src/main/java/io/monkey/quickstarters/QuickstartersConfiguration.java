package io.monkey.quickstarters;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.monkey.Configuration;
import io.monkey.datasource.DataSourceFactory;
import io.monkey.jaxrs.setup.JaxrsDeploymentFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

/**
 * @author michael
 * created on 17/9/2
 */
public class QuickstartersConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @Valid
    @JsonProperty("jaxrs")
    private JaxrsDeploymentFactory jaxrsDeploymentFactory = new JaxrsDeploymentFactory();

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public JaxrsDeploymentFactory getJaxrsDeploymentFactory() {
        return jaxrsDeploymentFactory;
    }

    public void setJaxrsDeploymentFactory(JaxrsDeploymentFactory jaxrsDeploymentFactory) {
        this.jaxrsDeploymentFactory = jaxrsDeploymentFactory;
    }

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

}
