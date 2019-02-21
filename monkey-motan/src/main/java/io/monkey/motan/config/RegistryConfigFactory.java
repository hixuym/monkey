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
import com.weibo.api.motan.config.RegistryConfig;
import io.monkey.util.Duration;
import io.monkey.validation.PortRange;

/**
 * @author Michael
 * Created at: 2019/1/4 12:12
 */
public class RegistryConfigFactory {

    // 注册配置名称
    @JsonProperty
    private String name;

    // 注册协议
    @JsonProperty
    private String regProtocol;

    // 注册中心地址，支持多个ip+port，格式：ip1:port1,ip2:port2,ip3，如果没有port，则使用默认的port
    @JsonProperty
    private String address;

    // 注册中心缺省端口
    @JsonProperty
    @PortRange
    private Integer port;

    // 注册中心请求超时时间(毫秒)
    @JsonProperty
    private Duration requestTimeout;

    // 注册中心连接超时时间(毫秒)
    @JsonProperty
    private Duration connectTimeout;

    // 注册中心会话超时时间(毫秒)
    @JsonProperty
    private Duration registrySessionTimeout;

    // 失败后重试的时间间隔
    @JsonProperty
    private Integer registryRetryPeriod;

    // 启动时检查注册中心是否存在
    @JsonProperty
    private String check;

    // 在该注册中心上服务是否暴露
    @JsonProperty
    private Boolean register;

    // 在该注册中心上服务是否引用
    @JsonProperty
    private Boolean subscribe;

    // vintage的配置移除策略，@see #RegistryConfig#Excise
    @JsonProperty
    private String excise;

    public RegistryConfig build() {
        RegistryConfig registryConfig = new RegistryConfig();

        registryConfig.setAddress(address);
        registryConfig.setCheck(check);
        registryConfig.setRegProtocol(regProtocol);
        registryConfig.setExcise(excise);
        registryConfig.setName(name);
        registryConfig.setPort(port);
        registryConfig.setRegister(register);
        registryConfig.setSubscribe(subscribe);
        registryConfig.setRegistryRetryPeriod(registryRetryPeriod);

        if (connectTimeout != null)
            registryConfig.setConnectTimeout((int) connectTimeout.toMilliseconds());
        if (registrySessionTimeout != null)
            registryConfig.setRegistrySessionTimeout((int) registrySessionTimeout.toMilliseconds());
        if (requestTimeout != null)
            registryConfig.setRequestTimeout((int) requestTimeout.toMilliseconds());

        return registryConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegProtocol() {
        return regProtocol;
    }

    public void setRegProtocol(String regProtocol) {
        this.regProtocol = regProtocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getRegistrySessionTimeout() {
        return registrySessionTimeout;
    }

    public void setRegistrySessionTimeout(Duration registrySessionTimeout) {
        this.registrySessionTimeout = registrySessionTimeout;
    }

    public Integer getRegistryRetryPeriod() {
        return registryRetryPeriod;
    }

    public void setRegistryRetryPeriod(Integer registryRetryPeriod) {
        this.registryRetryPeriod = registryRetryPeriod;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public String getExcise() {
        return excise;
    }

    public void setExcise(String excise) {
        this.excise = excise;
    }

}
