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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.util.concurrent.MoreExecutors;
import io.sunflower.db.TimeBoundHealthCheck;
import io.sunflower.util.Duration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SqlSessionFactoryHealthCheck extends HealthCheck {

  private final SqlSessionFactory sqlSessionFactory;
  private final String validationQuery;
  private final TimeBoundHealthCheck timeBoundHealthCheck;

  public SqlSessionFactoryHealthCheck(SqlSessionFactory sqlSessionFactory,
      String validationQuery) {
    this(MoreExecutors.newDirectExecutorService(), Duration.seconds(0), sqlSessionFactory,
        validationQuery);
  }

  public SqlSessionFactoryHealthCheck(ExecutorService executorService,
      Duration duration,
      SqlSessionFactory sqlSessionFactory,
      String validationQuery) {
    this.sqlSessionFactory = sqlSessionFactory;
    this.validationQuery = validationQuery;
    this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
  }

  @Override
  protected Result check() throws Exception {
    return timeBoundHealthCheck.check(() -> {
      SqlSession sqlSession = sqlSessionFactory.openSession();
      try {
        Connection conn = sqlSession.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(validationQuery);
        preparedStatement.executeQuery();
        sqlSession.commit();
      } catch (Exception e) {
        sqlSession.rollback();
        throw e;
      } finally {
        sqlSession.close();
      }

      return Result.healthy();
    });
  }
}
