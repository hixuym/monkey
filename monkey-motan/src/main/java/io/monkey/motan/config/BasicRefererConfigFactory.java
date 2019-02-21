/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.motan.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weibo.api.motan.config.AbstractRefererConfig;
import com.weibo.api.motan.config.BasicRefererInterfaceConfig;
import com.weibo.api.motan.config.RefererConfig;

import java.util.List;

/**
 * @author Michael
 * Created at: 2019/1/4 12:25
 */
public class BasicRefererConfigFactory extends AbstractInterfaceConfigFactory {

    // 服务接口的mock类SLA
    @JsonProperty
    private String mean;
    @JsonProperty
    private String p90;
    @JsonProperty
    private String p99;
    @JsonProperty
    private String p999;
    @JsonProperty
    private String errorRate;
    @JsonProperty
    private Boolean asyncInitConnection;

    public BasicRefererInterfaceConfig buildBasicReferer() {

        BasicRefererInterfaceConfig basicRefererInterfaceConfig = new BasicRefererInterfaceConfig();

        apply(basicRefererInterfaceConfig);

        basicRefererInterfaceConfig.setMean(mean);
        basicRefererInterfaceConfig.setP90(p90);
        basicRefererInterfaceConfig.setP99(p99);
        basicRefererInterfaceConfig.setP999(p999);

        return basicRefererInterfaceConfig;
    }

    public RefererConfig buildReferer() {

        RefererConfig refererConfig = new RefererConfig();

        apply(refererConfig);

        refererConfig.setMean(mean);
        refererConfig.setP90(p90);
        refererConfig.setP99(p99);
        refererConfig.setP999(p999);

        return refererConfig;
    }


    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getP90() {
        return p90;
    }

    public void setP90(String p90) {
        this.p90 = p90;
    }

    public String getP99() {
        return p99;
    }

    public void setP99(String p99) {
        this.p99 = p99;
    }

    public String getP999() {
        return p999;
    }

    public void setP999(String p999) {
        this.p999 = p999;
    }

    public String getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate;
    }

    public Boolean getAsyncInitConnection() {
        return asyncInitConnection;
    }

    public void setAsyncInitConnection(Boolean asyncInitConnection) {
        this.asyncInitConnection = asyncInitConnection;
    }
}
