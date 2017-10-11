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

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.matcher.Matchers.not;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.AbstractMatcher;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

public class MybatisModule extends AbstractModule {

  private static final AbstractMatcher<Method> DECLARED_BY_OBJECT = new AbstractMatcher<Method>() {
    @Override
    public boolean matches(Method method) {
      return method.getDeclaringClass() == Object.class;
    }
  };

  private static final AbstractMatcher<Method> SYNTHETIC = new AbstractMatcher<Method>() {
    @Override
    public boolean matches(Method method) {
      return method.isSynthetic();
    }
  };

  private final SqlSessionFactory sqlSessionFactory;

  private final List<Class<?>> mappers;

  public MybatisModule(SqlSessionFactory sqlSessionFactory, List<Class<?>> mappers) {
    this.sqlSessionFactory = sqlSessionFactory;
    this.mappers = mappers;
  }

  @Override
  protected void configure() {
    bind(SqlSessionFactory.class).toInstance(sqlSessionFactory);
    // sql session manager
    bind(SqlSessionManager.class).toProvider(SqlSessionManagerProvider.class).in(Scopes.SINGLETON);
    bind(SqlSession.class).to(SqlSessionManager.class).in(Scopes.SINGLETON);

    for (Class<?> mapper : mappers) {
      bind(mapper).toProvider(new MapperProvider(mapper));
    }

    bindTransactionInterceptors();
  }

  /**
   * bind transactional interceptors.
   */
  private void bindTransactionInterceptors() {
    // transactional interceptor
    TransactionalMethodInterceptor interceptor = new TransactionalMethodInterceptor();
    requestInjection(interceptor);
    bindInterceptor(any(),
        not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(annotatedWith(Transactional.class)),
        interceptor);
    // Intercept classes annotated with Transactional, but avoid "double"
    // interception when a mathod is also annotated inside an annotated
    // class.
    bindInterceptor(annotatedWith(Transactional.class),
        not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(not(annotatedWith(Transactional.class))),
        interceptor);
  }
}
