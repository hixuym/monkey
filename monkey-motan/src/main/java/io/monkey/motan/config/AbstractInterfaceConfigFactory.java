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
import com.weibo.api.motan.config.AbstractInterfaceConfig;
import com.weibo.api.motan.config.MethodConfig;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael
 * Created at: 2019/1/4 12:27
 */
public class AbstractInterfaceConfigFactory {

    @JsonProperty
    private String registry;

    @JsonProperty
    private String protocol;

    @JsonProperty
    private String id;

    // 应用名称
    @JsonProperty
    private String application;

    // 模块名称
    @JsonProperty
    private String module;

    // 分组
    @JsonProperty
    private String group;

    // 服务版本
    @JsonProperty
    private String version;

    // 代理类型
    @JsonProperty
    private String proxy;

    // 过滤器
    @JsonProperty
    private String filter;

    // 最大并发调用
    private Integer actives;

    // 是否异步
    @JsonProperty
    private Boolean async;

    // 服务接口的失败mock实现类名
    @JsonProperty
    private String mock;

    // 是否共享 channel
    @JsonProperty
    private Boolean shareChannel;

    // if throw exception when call failure，the default value is ture
    @JsonProperty
    private Boolean throwException;

    // 请求超时时间
    @JsonProperty
    private Integer requestTimeout;

    // 是否注册
    @JsonProperty
    private Boolean register;

    // 是否记录访问日志，true记录，false不记录
    @JsonProperty
    private String accessLog;

    // 是否进行check，如果为true，则在监测失败后抛异常
    @JsonProperty
    private String check;

    // 重试次数
    @JsonProperty
    private Integer retries;

    // 是否开启gzip压缩
    @JsonProperty
    private Boolean usegz;

    // 进行gzip压缩的最小阈值，usegz开启，且大于此值时才进行gzip压缩。单位Byte
    @JsonProperty
    private Integer mingzSize;

    @JsonProperty
    private String codec;

    @JsonProperty
    private String localServiceAddress;

    @JsonProperty
    private Integer backupRequestDelayTime;

    @JsonProperty
    private String backupRequestDelayRatio;

    @JsonProperty
    private String backupRequestSwitcherName;

    @JsonProperty
    private String backupRequestMaxRetryRatio;

    // 是否需要传输rpc server 端业务异常栈。默认true
    @JsonProperty
    private Boolean transExceptionStack;

    @JsonProperty("methods")
    private List<MethodConfigFactory> methodConfigFactory;

    void apply(AbstractInterfaceConfig config) {

        if (methodConfigFactory != null) {

            List<MethodConfig> methodConfigs =
                methodConfigFactory.stream().map(MethodConfigFactory::build).collect(Collectors.toList());

            if (config instanceof ServiceConfig)
                ((ServiceConfig) config).setMethods(methodConfigs);
            if (config instanceof RefererConfig)
                ((RefererConfig) config).setMethods(methodConfigs);
        }

        config.setId(id);
        config.setAccessLog(accessLog);
        config.setActives(actives);
        config.setApplication(application);
        config.setModule(module);
        config.setAsync(async);
        config.setBackupRequestDelayRatio(backupRequestDelayRatio);
        config.setBackupRequestSwitcherName(backupRequestSwitcherName);
        config.setBackupRequestDelayTime(backupRequestDelayTime);
        config.setBackupRequestMaxRetryRatio(backupRequestMaxRetryRatio);

        config.setCheck(check);
        config.setVersion(version);
        config.setUsegz(usegz);
        config.setCodec(codec);
        config.setTransExceptionStack(transExceptionStack);
        config.setThrowException(throwException);
        config.setShareChannel(shareChannel);
        config.setRequestTimeout(requestTimeout);
        config.setRegister(register);
        config.setRetries(retries);
        config.setProxy(proxy);
        config.setMock(mock);
        config.setMingzSize(mingzSize);
        config.setGroup(group);
        config.setFilter(filter);
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public Boolean getShareChannel() {
        return shareChannel;
    }

    public void setShareChannel(Boolean shareChannel) {
        this.shareChannel = shareChannel;
    }

    public Boolean getThrowException() {
        return throwException;
    }

    public void setThrowException(Boolean throwException) {
        this.throwException = throwException;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getAccessLog() {
        return accessLog;
    }

    public void setAccessLog(String accessLog) {
        this.accessLog = accessLog;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getUsegz() {
        return usegz;
    }

    public void setUsegz(Boolean usegz) {
        this.usegz = usegz;
    }

    public Integer getMingzSize() {
        return mingzSize;
    }

    public void setMingzSize(Integer mingzSize) {
        this.mingzSize = mingzSize;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getLocalServiceAddress() {
        return localServiceAddress;
    }

    public void setLocalServiceAddress(String localServiceAddress) {
        this.localServiceAddress = localServiceAddress;
    }

    public Integer getBackupRequestDelayTime() {
        return backupRequestDelayTime;
    }

    public void setBackupRequestDelayTime(Integer backupRequestDelayTime) {
        this.backupRequestDelayTime = backupRequestDelayTime;
    }

    public String getBackupRequestDelayRatio() {
        return backupRequestDelayRatio;
    }

    public void setBackupRequestDelayRatio(String backupRequestDelayRatio) {
        this.backupRequestDelayRatio = backupRequestDelayRatio;
    }

    public String getBackupRequestSwitcherName() {
        return backupRequestSwitcherName;
    }

    public void setBackupRequestSwitcherName(String backupRequestSwitcherName) {
        this.backupRequestSwitcherName = backupRequestSwitcherName;
    }

    public String getBackupRequestMaxRetryRatio() {
        return backupRequestMaxRetryRatio;
    }

    public void setBackupRequestMaxRetryRatio(String backupRequestMaxRetryRatio) {
        this.backupRequestMaxRetryRatio = backupRequestMaxRetryRatio;
    }

    public Boolean getTransExceptionStack() {
        return transExceptionStack;
    }

    public void setTransExceptionStack(Boolean transExceptionStack) {
        this.transExceptionStack = transExceptionStack;
    }

    public List<MethodConfigFactory> getMethodConfigFactory() {
        return methodConfigFactory;
    }

    public void setMethodConfigFactory(List<MethodConfigFactory> methodConfigFactory) {
        this.methodConfigFactory = methodConfigFactory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
