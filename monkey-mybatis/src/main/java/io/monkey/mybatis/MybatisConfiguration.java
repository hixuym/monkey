/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.monkey.mybatis;

import io.micronaut.context.annotation.EachProperty;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 * Created at: 2019/2/19 15:47
 */
@EachProperty(value = MybatisConfiguration.PREFIX, primary = "default")
public class MybatisConfiguration {
    public static final String PREFIX = "mybatis";

    private boolean lazyLoadingEnabled = false;
    private boolean aggressiveLazyLoading = true;
    private boolean multipleResultSetsEnabled = true;
    private boolean useGeneratedKeys = false;
    private boolean useColumnLabel = true;
    private boolean cacheEnabled = true;
    private ExecutorType defaultExecutorType = ExecutorType.SIMPLE;
    private AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;
    private boolean callSettersOnNulls = false;
    private Integer defaultStatementTimeout;
    private boolean mapUnderscoreToCamelCase = false;
    private boolean failFast = false;

    private String[] packagesToScan;

    private Map<String, Class> aliases = new HashMap<>();

    public Configuration build() {
        Configuration configuration = new Configuration();
        configuration.setLazyLoadingEnabled(lazyLoadingEnabled);
        configuration.setAggressiveLazyLoading(aggressiveLazyLoading);
        configuration.setMultipleResultSetsEnabled(multipleResultSetsEnabled);
        configuration.setUseGeneratedKeys(useGeneratedKeys);
        configuration.setUseColumnLabel(useColumnLabel);
        configuration.setCacheEnabled(cacheEnabled);
        configuration.setDefaultExecutorType(defaultExecutorType);
        configuration.setAutoMappingBehavior(autoMappingBehavior);
        configuration.setCallSettersOnNulls(callSettersOnNulls);
        configuration.setDefaultStatementTimeout(defaultStatementTimeout);
        configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);

        if (failFast) {
            configuration.getMappedStatementNames();
        }

        aliases.forEach((alias, type) -> configuration.getTypeAliasRegistry().registerAlias(alias, type));

        return configuration;
    }

    public boolean isLazyLoadingEnabled() {
        return lazyLoadingEnabled;
    }

    public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
        this.lazyLoadingEnabled = lazyLoadingEnabled;
    }

    public boolean isAggressiveLazyLoading() {
        return aggressiveLazyLoading;
    }

    public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
        this.aggressiveLazyLoading = aggressiveLazyLoading;
    }

    public boolean isMultipleResultSetsEnabled() {
        return multipleResultSetsEnabled;
    }

    public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
        this.multipleResultSetsEnabled = multipleResultSetsEnabled;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public AutoMappingBehavior getAutoMappingBehavior() {
        return autoMappingBehavior;
    }

    public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
        this.autoMappingBehavior = autoMappingBehavior;
    }

    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public Map<String, Class> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, Class> aliases) {
        this.aliases = aliases;
    }

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }
}
