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

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.db.DatabaseConfiguration;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.util.Duration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.SqlSessionFactory;

public abstract class MybatisBundle<T extends Configuration> implements ConfiguredBundle<T>,
    DatabaseConfiguration<T> {

  static final String DEFAULT_NAME = "mybatis";

  private final SqlSessionFactoryFactory sqlSessionFactoryFactory;

  private final String[] scanPkgs;

  protected MybatisBundle(String... scanPkgs) {
    this(new SqlSessionFactoryFactory(), scanPkgs);
  }

  protected MybatisBundle(SqlSessionFactoryFactory sqlSessionFactoryFactory, String... scanPkgs) {
    this.scanPkgs = scanPkgs;
    this.sqlSessionFactoryFactory = sqlSessionFactoryFactory;
  }

  protected String name() {
    return DEFAULT_NAME;
  }

  @Override
  public void run(T configuration, Environment environment) throws Exception {
    PooledDataSourceFactory dataSourceFactory = getDataSourceFactory(configuration);

    ImmutableList.Builder<Class<?>> builder = new ImmutableList.Builder<>();

    ResolverUtil util = new ResolverUtil();

    util.setClassLoader(environment.classLoader());

    ResolverUtil.Test isMapper = new ResolverUtil.AnnotatedWith(Mapper.class);

    for (String pkg : scanPkgs) {
      util.find(isMapper, pkg);
    }

    String pkgs = dataSourceFactory.getProperties().get("search.packages");

    if (StringUtils.isNotEmpty(pkgs)) {

      List<String> scanPkgs = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(pkgs);

      for (String pkg : scanPkgs) {
        util.find(isMapper, pkg);
      }
    }

    builder.addAll(util.getClasses());

    List<Class<?>> mappers = builder.build();

    SqlSessionFactory sqlSessionFactory =
        sqlSessionFactoryFactory.build(this,
            environment,
            dataSourceFactory,
            mappers,
            name());

    environment.guicey().install(new MybatisModule(sqlSessionFactory, mappers));

    environment.healthChecks().register(name(), new SqlSessionFactoryHealthCheck(
        environment.getHealthCheckExecutorService(),
        dataSourceFactory.getValidationQueryTimeout().orElse(Duration.seconds(5)),
        sqlSessionFactory,
        dataSourceFactory.getValidationQuery()
    ));
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }

  protected void configure(org.apache.ibatis.session.Configuration config) {
  }
}
