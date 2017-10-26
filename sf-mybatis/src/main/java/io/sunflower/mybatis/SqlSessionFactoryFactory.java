/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.mybatis;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.sunflower.db.ManagedDataSource;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.setup.Environment;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michael
 */
public class SqlSessionFactoryFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryFactory.class);

  public SqlSessionFactory build(MybatisBundle<?> bundle,
      Environment environment,
      PooledDataSourceFactory dbConfig,
      List<Class<?>> mappers) {
    return build(bundle, environment, dbConfig, mappers, MybatisBundle.DEFAULT_NAME);
  }

  public SqlSessionFactory build(MybatisBundle<?> bundle,
      Environment environment,
      PooledDataSourceFactory dbConfig,
      List<Class<?>> mappers,
      String name) {
    final ManagedDataSource dataSource = dbConfig.build(environment.metrics(), name);

    environment.lifecycle().manage(new DataSourceManager(dataSource));

    return build(bundle, environment, dbConfig, dataSource, mappers, name);
  }

  public SqlSessionFactory build(MybatisBundle<?> bundle,
      Environment environment,
      PooledDataSourceFactory dbConfig,
      ManagedDataSource dataSource,
      List<Class<?>> mappers,
      String name) {

    Properties properties = new Properties();

    for (Map.Entry<String, String> e : dbConfig.getProperties().entrySet()) {
      properties.setProperty(e.getKey(), e.getValue());
    }

    org.apache.ibatis.mapping.Environment mybatisEnv =
        new org.apache.ibatis.mapping.Environment(name,
            getTransactionFactory(),
            dbConfig.build(environment.metrics(), name));

    Configuration configuration = new Configuration(mybatisEnv);

    new ConfigurationBuilder(configuration).doConfigure(properties);

    mappers.forEach(configuration::addMapper);

    bundle.configure(configuration);

    return new SqlSessionFactoryBuilder().build(configuration);
  }

  private static class ConfigurationBuilder extends BaseBuilder {

    ConfigurationBuilder(Configuration configuration) {
      super(configuration);
    }

    void doConfigure(Properties props) {
      configuration.setAutoMappingBehavior(
          AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
      configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior
          .valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
      configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
      configuration
          .setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
      configuration.setAggressiveLazyLoading(
          booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
      configuration.setMultipleResultSetsEnabled(
          booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
      configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
      configuration
          .setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
      configuration.setDefaultExecutorType(
          ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
      configuration.setDefaultStatementTimeout(
          integerValueOf(props.getProperty("defaultStatementTimeout"), null));
      configuration
          .setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
      configuration.setMapUnderscoreToCamelCase(
          booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
      configuration.setSafeRowBoundsEnabled(
          booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
      configuration.setLocalCacheScope(
          LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
      configuration
          .setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
      configuration.setLazyLoadTriggerMethods(
          stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"),
              "equals,clone,hashCode,toString"));
      configuration.setSafeResultHandlerEnabled(
          booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
      configuration
          .setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
      @SuppressWarnings("unchecked")
      Class<? extends TypeHandler> typeHandler = (Class<? extends TypeHandler>) resolveClass(
          props.getProperty("defaultEnumTypeHandler"));
      configuration.setDefaultEnumTypeHandler(typeHandler);
      configuration
          .setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
      configuration
          .setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
      configuration.setReturnInstanceForEmptyRow(
          booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
      configuration.setLogPrefix(props.getProperty("logPrefix"));
      @SuppressWarnings("unchecked")
      Class<? extends Log> logImpl = (Class<? extends Log>) resolveClass(
          props.getProperty("logImpl"));
      configuration.setLogImpl(logImpl);
      configuration
          .setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
    }
  }

  protected TransactionFactory getTransactionFactory() {
    return new JdbcTransactionFactory();
  }
}
