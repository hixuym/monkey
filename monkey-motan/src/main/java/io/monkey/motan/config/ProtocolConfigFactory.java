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
import com.weibo.api.motan.config.ProtocolConfig;

import java.util.Map;

/**
 * @author Michael
 * Created at: 2019/1/4 12:19
 */
public class ProtocolConfigFactory {

    // 服务协议
    @JsonProperty
    private String name;

    // 序列化方式
    @JsonProperty
    private String serialization;

    // 协议编码
    @JsonProperty
    private String codec;

    // IO线程池大小
    @JsonProperty
    private Integer iothreads;
    // 请求超时
    @JsonProperty
    private Integer requestTimeout;
    // client最小连接数
    @JsonProperty
    private Integer minClientConnection;
    // client最大连接数
    @JsonProperty
    private Integer maxClientConnection;
    // 最小工作pool线程数
    @JsonProperty
    private Integer minWorkerThread;
    // 最大工作pool线程数
    @JsonProperty
    private Integer maxWorkerThread;
    // 请求响应包的最大长度限制
    @JsonProperty
    private Integer maxContentLength;
    // server支持的最大连接数
    @JsonProperty
    private Integer maxServerConnection;

    // 连接池管理方式，是否lifo
    @JsonProperty
    private Boolean poolLifo;
    // 是否延迟init
    @JsonProperty
    private Boolean lazyInit;

    // endpoint factory
    @JsonProperty
    private String endpointFactory;

    // 采用哪种cluster 的实现
    @JsonProperty
    private String cluster;
    // loadbalance 方式
    @JsonProperty
    private String loadbalance;
    // high available strategy
    @JsonProperty
    private String haStrategy;
    // server worker queue size
    @JsonProperty
    private Integer workerQueueSize;
    // server accept connections count
    @JsonProperty
    private Integer acceptConnections;

    // proxy type, like jdk or javassist
    @JsonProperty
    private String proxy;
    // filter, 多个filter用","分割，blank string 表示采用默认的filter配置
    @JsonProperty
    private String filter;
    // retry count if call failure
    @JsonProperty
    private Integer retries;
    // if the request is called async, a taskFuture result will be sent back.
    @JsonProperty
    private Boolean async;

    // 扩展参数
    @JsonProperty
    private Map<String, String> parameters;

    public ProtocolConfig build() {
        ProtocolConfig protocolConfig = new ProtocolConfig();

        protocolConfig.setName(name);
        protocolConfig.setAcceptConnections(acceptConnections);
        protocolConfig.setAsync(async);
        protocolConfig.setCluster(cluster);
        protocolConfig.setCodec(codec);
        protocolConfig.setEndpointFactory(endpointFactory);
        protocolConfig.setFilter(filter);
        protocolConfig.setHaStrategy(haStrategy);
        protocolConfig.setIothreads(iothreads);
        protocolConfig.setLazyInit(lazyInit);
        protocolConfig.setMaxClientConnection(maxClientConnection);
        protocolConfig.setMaxServerConnection(maxServerConnection);
        protocolConfig.setLoadbalance(loadbalance);
        protocolConfig.setWorkerQueueSize(workerQueueSize);
        protocolConfig.setSerialization(serialization);
        protocolConfig.setRetries(retries);
        protocolConfig.setRequestTimeout(requestTimeout);
        protocolConfig.setProxy(proxy);
        protocolConfig.setPoolLifo(poolLifo);
        protocolConfig.setParameters(parameters);
        protocolConfig.setMinClientConnection(minClientConnection);
        protocolConfig.setMinWorkerThread(minWorkerThread);
        protocolConfig.setMaxContentLength(maxContentLength);

        return protocolConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getMinClientConnection() {
        return minClientConnection;
    }

    public void setMinClientConnection(Integer minClientConnection) {
        this.minClientConnection = minClientConnection;
    }

    public Integer getMaxClientConnection() {
        return maxClientConnection;
    }

    public void setMaxClientConnection(Integer maxClientConnection) {
        this.maxClientConnection = maxClientConnection;
    }

    public Integer getMinWorkerThread() {
        return minWorkerThread;
    }

    public void setMinWorkerThread(Integer minWorkerThread) {
        this.minWorkerThread = minWorkerThread;
    }

    public Integer getMaxWorkerThread() {
        return maxWorkerThread;
    }

    public void setMaxWorkerThread(Integer maxWorkerThread) {
        this.maxWorkerThread = maxWorkerThread;
    }

    public Integer getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(Integer maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public Integer getMaxServerConnection() {
        return maxServerConnection;
    }

    public void setMaxServerConnection(Integer maxServerConnection) {
        this.maxServerConnection = maxServerConnection;
    }

    public Boolean getPoolLifo() {
        return poolLifo;
    }

    public void setPoolLifo(Boolean poolLifo) {
        this.poolLifo = poolLifo;
    }

    public Boolean getLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(Boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getEndpointFactory() {
        return endpointFactory;
    }

    public void setEndpointFactory(String endpointFactory) {
        this.endpointFactory = endpointFactory;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public String getHaStrategy() {
        return haStrategy;
    }

    public void setHaStrategy(String haStrategy) {
        this.haStrategy = haStrategy;
    }

    public Integer getWorkerQueueSize() {
        return workerQueueSize;
    }

    public void setWorkerQueueSize(Integer workerQueueSize) {
        this.workerQueueSize = workerQueueSize;
    }

    public Integer getAcceptConnections() {
        return acceptConnections;
    }

    public void setAcceptConnections(Integer acceptConnections) {
        this.acceptConnections = acceptConnections;
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

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
