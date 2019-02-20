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

import io.micronaut.context.BeanLocator;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Requires;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import io.monkey.mybatis.binder.SimpleMapperRegistry;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael
 * Created at: 2019/2/19 15:47
 */
@EachProperty(value = MybatisConfiguration.PREFIX, primary = "default")
@Requires(property = MybatisConfiguration.PREFIX + ".default")
@Requires(property = BasicJdbcConfiguration.PREFIX + ".default")
public class MybatisConfiguration extends Configuration {
    public static final String PREFIX = "mybatis";

    private boolean failFast = false;

    private Map<String, Class> aliases = new HashMap<>();

    private final SimpleMapperRegistry mapperRegistry = new SimpleMapperRegistry(this);

    private final String dataSourceName;

    public MybatisConfiguration(@Parameter String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @PostConstruct
    public void init(DataSource dataSource, BeanLocator beanLocator) {

        if (failFast) {
            this.getMappedStatementNames();
        }

        aliases.forEach((alias, type) -> this.getTypeAliasRegistry().registerAlias(alias, type));

        TransactionFactory transactionFactory = beanLocator.findBean(TransactionFactory.class,
            Qualifiers.byName(dataSourceName)).orElse(new JdbcTransactionFactory());

        Environment environment = new Environment.Builder(dataSourceName)
            .dataSource(dataSource)
            .transactionFactory(transactionFactory)
            .build();

        beanLocator.streamOfType(Interceptor.class,
            Qualifiers.byName(dataSourceName)).forEach(this::addInterceptor);

        DatabaseIdProvider databaseIdProvider =  beanLocator.findBean(DatabaseIdProvider.class,
            Qualifiers.byName(dataSourceName)).orElse(new VendorDatabaseIdProvider());

        this.setEnvironment(environment);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMappers(String packageName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        this.mapperRegistry.addMapper(type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return this.mapperRegistry.hasMapper(type);
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

    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }
}
