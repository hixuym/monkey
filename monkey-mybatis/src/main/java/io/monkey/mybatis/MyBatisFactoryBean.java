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
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

/**
 * @author Michael
 * Created at: 2019/2/19 15:58
 */
@Factory
public class MyBatisFactoryBean {

    private final MybatisConfiguration mybatisConfiguration;
    private final BeanLocator beanLocator;

    public MyBatisFactoryBean(MybatisConfiguration mybatisConfiguration,
                              BeanLocator beanLocator) {
        this.mybatisConfiguration = mybatisConfiguration;
        this.beanLocator = beanLocator;
    }

    @EachBean(DataSource.class)
    protected Configuration mybatisConfiguration(@Parameter String dataSourceName,
                                        DataSource dataSource) {

        MybatisConfiguration mybatisConfiguration = beanLocator.findBean(MybatisConfiguration.class,
            Qualifiers.byName(dataSourceName)).orElse(this.mybatisConfiguration);

        TransactionFactory transactionFactory = beanLocator.findBean(TransactionFactory.class,
            Qualifiers.byName(dataSourceName)).orElse(new JdbcTransactionFactory());

        Environment environment = new Environment.Builder(dataSourceName)
            .dataSource(dataSource)
            .transactionFactory(transactionFactory)
            .build();

        Configuration configuration = mybatisConfiguration.build();

        beanLocator.streamOfType(Interceptor.class,
            Qualifiers.byName(dataSourceName)).forEach(configuration::addInterceptor);

        DatabaseIdProvider databaseIdProvider =  beanLocator.findBean(DatabaseIdProvider.class,
            Qualifiers.byName(dataSourceName)).orElse(new VendorDatabaseIdProvider());

        configuration.setEnvironment(environment);

        return configuration;
    }

    @EachBean(Configuration.class)
    protected SqlSessionFactory mybatisSqlSessionFactory(Configuration configuration) {
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @EachBean(SqlSessionFactory.class)
    protected SqlSessionFactory mybatisSqlSessionManager(SqlSessionFactory sqlSessionFactory) {
        return SqlSessionManager.newInstance(sqlSessionFactory);
    }
}
