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

package io.monkey.motan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.*;
import com.weibo.api.motan.exception.MotanErrorMsgConstant;
import com.weibo.api.motan.exception.MotanFrameworkException;
import com.weibo.api.motan.util.CollectionUtil;
import com.weibo.api.motan.util.MotanFrameworkUtil;
import io.monkey.motan.config.BasicRefererConfigFactory;
import io.monkey.motan.config.BasicServiceConfigFactory;
import io.monkey.motan.config.ProtocolConfigFactory;
import io.monkey.motan.config.RegistryConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Michael
 * Created at: 2019/1/4 10:59
 */
public class MotanFactory {

    @JsonProperty("registries")
    private List<RegistryConfigFactory> registryConfigFactory;

    @JsonProperty("protocols")
    private List<ProtocolConfigFactory> protocolConfigFactory;

    @JsonProperty("basicService")
    private BasicServiceConfigFactory basicServiceConfigFactory;

    @JsonProperty("basicReferer")
    private BasicRefererConfigFactory basicRefererConfigFactory;

    @JsonProperty("services")
    private List<BasicServiceConfigFactory> servicesConfigFactory;

    @JsonProperty("referers")
    private List<BasicRefererConfigFactory> referersConfigFactory;

    @JsonIgnore
    private Map<String, RegistryConfig> registryConfigMap;

    @JsonIgnore
    private Map<String, ProtocolConfig> protocolConfigMap;

    @JsonIgnore
    private Map<String, ServiceConfig> serviceConfigMap = Maps.newHashMap();

    @JsonIgnore
    private boolean serviceConfiged = false;

    @JsonIgnore
    private Map<String, RefererConfig> refererConfigMap = Maps.newHashMap();

    @JsonIgnore
    private boolean refererConfiged = false;

    @JsonIgnore
    private BasicRefererInterfaceConfig basicRefererInterfaceConfig;

    @JsonIgnore
    private BasicServiceInterfaceConfig basicServiceInterfaceConfig;

    public void registerReferer(String id, RefererConfig refererConfig) {
        if (!getReferersConfig().containsKey(id)) {
            checkAndConfigReferer(refererConfig);
            this.refererConfigMap.put(id, refererConfig);
        }
    }

    public void registerService(String id, ServiceConfig serviceConfig) {
        if (!getServicesConfig().containsKey(id)) {
            checkAndConfigService(serviceConfig);
            this.serviceConfigMap.put(id, serviceConfig);
        }
    }

    @JsonIgnore
    public Map<String, RegistryConfig> getRegistryConfig() {

        if (registryConfigMap != null) {
            return registryConfigMap;
        }

        this.registryConfigMap = Maps.newHashMap();

        if (registryConfigFactory != null) {
            for (RegistryConfigFactory registryConfigFactory :registryConfigFactory) {
                RegistryConfig registryConfig = registryConfigFactory.build();
                registryConfigMap.put(registryConfig.getName(), registryConfig);
            }
        }

        return registryConfigMap;
    }

    @JsonIgnore
    public Map<String, ProtocolConfig> getProtocolConfig() {
        if (protocolConfigMap != null) {
            return protocolConfigMap;
        }

        this.protocolConfigMap = Maps.newHashMap();

        if (protocolConfigFactory != null) {
            for (ProtocolConfigFactory protocolConfigFactory :protocolConfigFactory) {
                ProtocolConfig protocolConfig = protocolConfigFactory.build();
                protocolConfigMap.put(protocolConfig.getName(), protocolConfig);
            }
        }

        return protocolConfigMap;
    }

    @JsonIgnore
    public BasicRefererInterfaceConfig getBasicRefererConfig() {

        if (basicRefererConfigFactory != null) {
            basicRefererInterfaceConfig = basicRefererConfigFactory.buildBasicReferer();

            basicRefererInterfaceConfig.setRegistries(getRegistryConfig(basicRefererConfigFactory.getRegistry()));
            basicRefererInterfaceConfig.setProtocols(getProtocolConfig(basicRefererConfigFactory.getProtocol()));
        }

        return basicRefererInterfaceConfig;
    }

    @JsonIgnore
    public BasicServiceInterfaceConfig getBasicServiceConfig() {

        if (basicServiceConfigFactory != null) {
            basicServiceInterfaceConfig = basicServiceConfigFactory.buildBasicService();
            String protocol = ConfigUtil.extractProtocols(basicServiceInterfaceConfig.getExport());
            basicServiceInterfaceConfig.setRegistries(getRegistryConfig(basicServiceConfigFactory.getRegistry()));
            basicServiceInterfaceConfig.setProtocols(getProtocolConfig(protocol));
        }

        return basicServiceInterfaceConfig;
    }

    @JsonIgnore
    public Map<String, RefererConfig> getReferersConfig() {

        if (referersConfigFactory != null && !refererConfiged) {
            for (BasicRefererConfigFactory refererConfigFactory :referersConfigFactory) {
                RefererConfig refererConfig = refererConfigFactory.buildReferer();

                refererConfig.setRegistries(getRegistryConfig(refererConfigFactory.getRegistry()));
                refererConfig.setProtocols(getProtocolConfig(refererConfigFactory.getProtocol()));

                checkAndConfigReferer(refererConfig);

                refererConfigMap.put(refererConfig.getId(), refererConfig);
            }

            this.refererConfiged = true;
        }

        return refererConfigMap;
    }

    @JsonIgnore
    public Map<String, ServiceConfig> getServicesConfig() {

        if (servicesConfigFactory != null && !serviceConfiged) {
            for (BasicServiceConfigFactory serviceConfigFactory :servicesConfigFactory) {
                ServiceConfig serviceConfig = serviceConfigFactory.buildService();

                serviceConfig.setRegistries(getRegistryConfig(serviceConfigFactory.getRegistry()));
                serviceConfig.setProtocols(getProtocolConfig(serviceConfigFactory.getProtocol()));

                checkAndConfigService(serviceConfig);

                serviceConfigMap.put(serviceConfig.getId(), serviceConfig);
            }

            serviceConfiged = true;
        }

        return serviceConfigMap;
    }

    private void checkAndConfigReferer(RefererConfig refererConfig) {
        if (basicRefererInterfaceConfig != null) {
            refererConfig.setBasicReferer(basicRefererInterfaceConfig);

            if (CollectionUtil.isEmpty(refererConfig.getProtocols())) {
                refererConfig.setProtocols(basicRefererInterfaceConfig.getProtocols());
            }

            if (CollectionUtil.isEmpty(refererConfig.getRegistries())) {
                refererConfig.setRegistries(basicRefererInterfaceConfig.getRegistries());
            }
        }

        if (CollectionUtil.isEmpty(refererConfig.getProtocols())) {
            refererConfig.setProtocol(MotanFrameworkUtil.getDefaultProtocolConfig());
        }

        if (CollectionUtil.isEmpty(refererConfig.getRegistries())) {
            refererConfig.setRegistry(MotanFrameworkUtil.getDefaultRegistryConfig());
        }
    }

    private void checkAndConfigService(ServiceConfig serviceConfig) {

        if (basicServiceInterfaceConfig != null) {
            serviceConfig.setBasicService(basicServiceInterfaceConfig);
        }

        if (Strings.isNullOrEmpty(serviceConfig.getExport())
            && serviceConfig.getBasicService() != null
            && !Strings.isNullOrEmpty(serviceConfig.getBasicService().getExport())) {
            serviceConfig.setExport(serviceConfig.getBasicService().getExport());
            if (serviceConfig.getBasicService().getProtocols() != null) {
                serviceConfig.setProtocols(new ArrayList<>(serviceConfig.getBasicService().getProtocols()));
            }
        }

        if (CollectionUtil.isEmpty(serviceConfig.getProtocols())
            && !Strings.isNullOrEmpty(serviceConfig.getExport())) {

            Map<String, Integer> exportMap = ConfigUtil.parseExport(serviceConfig.getExport());
            if (!exportMap.isEmpty()) {
                List<ProtocolConfig> protos = new ArrayList<>();
                for (String p : exportMap.keySet()) {
                    ProtocolConfig proto = getProtocolConfig().get(p);
                    if (proto == null) {
                        if (MotanConstants.PROTOCOL_MOTAN.equals(p)) {
                            proto = MotanFrameworkUtil.getDefaultProtocolConfig();
                        } else {
                            throw new MotanFrameworkException(String.format("cann't find %s ProtocolConfig bean! export:%s", p, serviceConfig.getExport()),
                                MotanErrorMsgConstant.FRAMEWORK_INIT_ERROR);
                        }
                    }

                    protos.add(proto);
                }
                serviceConfig.setProtocols(protos);
            }
        }

        if (Strings.isNullOrEmpty(serviceConfig.getExport())
            || CollectionUtil.isEmpty(serviceConfig.getProtocols())) {
            throw new MotanFrameworkException(String.format("%s ServiceConfig must config right export value!",
                serviceConfig.getInterface().getName()),
                MotanErrorMsgConstant.FRAMEWORK_INIT_ERROR);
        }

        if (CollectionUtil.isEmpty(serviceConfig.getRegistries())
            && serviceConfig.getBasicService() != null
            && !CollectionUtil.isEmpty(serviceConfig.getBasicService().getRegistries())) {
            serviceConfig.setRegistries(serviceConfig.getBasicService().getRegistries());
        }

        if (CollectionUtil.isEmpty(serviceConfig.getRegistries())) {
            serviceConfig.setRegistry(MotanFrameworkUtil.getDefaultRegistryConfig());
        }

    }

    private List<RegistryConfig> getRegistryConfig(String names) {

        if (Strings.isNullOrEmpty(names)) {
            return null;
        }

        Iterable<String> iter = Splitter.on(",").trimResults().split(names);

        List<RegistryConfig> results = Lists.newArrayList();

        for (String s : iter) {

            if (getRegistryConfig().containsKey(s)) {
                results.add(getRegistryConfig().get(s));
            }

        }

        return results.isEmpty() ? null : results;

    }

    private List<ProtocolConfig> getProtocolConfig(String names) {

        if (Strings.isNullOrEmpty(names)) {
            return null;
        }

        Iterable<String> iter = Splitter.on(",").trimResults().split(names);

        List<ProtocolConfig> results = Lists.newArrayList();

        for (String s : iter) {

            if (getProtocolConfig().containsKey(s)) {
                results.add(getProtocolConfig().get(s));
            }

        }

        return results.isEmpty() ? null : results;

    }

    public List<RegistryConfigFactory> getRegistryConfigFactory() {
        return registryConfigFactory;
    }

    public void setRegistryConfigFactory(List<RegistryConfigFactory> registryConfigFactory) {
        this.registryConfigFactory = registryConfigFactory;
    }

    public List<ProtocolConfigFactory> getProtocolConfigFactory() {
        return protocolConfigFactory;
    }

    public void setProtocolConfigFactory(List<ProtocolConfigFactory> protocolConfigFactory) {
        this.protocolConfigFactory = protocolConfigFactory;
    }

    public BasicServiceConfigFactory getBasicServiceConfigFactory() {
        return basicServiceConfigFactory;
    }

    public void setBasicServiceConfigFactory(BasicServiceConfigFactory basicServiceConfigFactory) {
        this.basicServiceConfigFactory = basicServiceConfigFactory;
    }

    public BasicRefererConfigFactory getBasicRefererConfigFactory() {
        return basicRefererConfigFactory;
    }

    public void setBasicRefererConfigFactory(BasicRefererConfigFactory basicRefererConfigFactory) {
        this.basicRefererConfigFactory = basicRefererConfigFactory;
    }

    public List<BasicServiceConfigFactory> getServicesConfigFactory() {
        return servicesConfigFactory;
    }

    public void setServicesConfigFactory(List<BasicServiceConfigFactory> servicesConfigFactory) {
        this.servicesConfigFactory = servicesConfigFactory;
    }

    public List<BasicRefererConfigFactory> getReferersConfigFactory() {
        return referersConfigFactory;
    }

    public void setReferersConfigFactory(List<BasicRefererConfigFactory> referersConfigFactory) {
        this.referersConfigFactory = referersConfigFactory;
    }
}
