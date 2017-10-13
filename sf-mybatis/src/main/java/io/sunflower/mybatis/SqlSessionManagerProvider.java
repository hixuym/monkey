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

import static org.apache.ibatis.session.SqlSessionManager.newInstance;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

@Singleton
public final class SqlSessionManagerProvider implements Provider<SqlSessionManager> {

  private SqlSessionManager sqlSessionManager;

  /**
   * @since 1.0.1
   */
  public SqlSessionManagerProvider() {
    // do nothing
  }

  /**
   * Creates the new sql session manager.
   *
   * @param sqlSessionFactory the sql session factory
   * @since 1.0.1
   */
  @Inject
  public void createNewSqlSessionManager(SqlSessionFactory sqlSessionFactory) {
    this.sqlSessionManager = newInstance(sqlSessionFactory);
  }

  @Override
  public SqlSessionManager get() {
    return sqlSessionManager;
  }

}
