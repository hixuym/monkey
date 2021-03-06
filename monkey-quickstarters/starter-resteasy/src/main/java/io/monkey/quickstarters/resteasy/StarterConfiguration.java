/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.quickstarters.resteasy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.monkey.Configuration;
import io.monkey.datasource.DataSourceFactory;
import io.monkey.resteasy.ResteasyFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

/**
 * @author michael
 * created on 17/9/2
 */
public class StarterConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @Valid
    @JsonProperty("resteasy")
    private ResteasyFactory resteasyFactory = new ResteasyFactory();

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    public ResteasyFactory getResteasyFactory() {
        return resteasyFactory;
    }

    public void setResteasyFactory(ResteasyFactory resteasyFactory) {
        this.resteasyFactory = resteasyFactory;
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
